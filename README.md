# Word Distribution Tool

Word Distribution Tool which enables calculating the `distribution of occurrences of bags of words in a text`. 
The calculation is concurrent, with the ability to add new sources of text to analyze in real-time. 
The system makes it possible to calculate the distribution of the number of occurrences of bags of words for individual 
documents, as well as aggregation of the results of several previous calculations.

## System Description

The system functions as a *pipeline*, which has three types of nodes - **input**, **cruncher** and **output**. Input components provide input to the system, cruncher components provide data processing, while output components provide storage and display of system performance.

Input components always precede cruncher components, and cruncher components always precede output components.
These components can exist in arbitrarily many instances, and each instance runs on a separate thread. Their main task is to provide data flow through the system. Each component type has a thread pool attached to it, which provides the task for that component type. In addition, the components communicate with each other via shared blocking queues. Each cruncher and output component has its own blocking order in which its predecessors can insert processing elements.

### Input Component

The main task of the input components is to provide the data that the cruncher components process. Each input component is bound to the *input* blocking queue of one or more cruncher components. The input component produces objects for that queue, ie. there is a *producer* over that queue, while the cruncher components will be a *consumer* each over its own queue.

`FileInput` component provides reading of *ASCII* encoded files. This component is given a list of directories to be visited, as well as a disk that contains those files. It recursively navigates the directories that are listed as input. When it finds any `.txt` file in one of them, the file is added to the reading list. Reading a single file is done as a separate job within the `Thread Pool`, which is intended for all `FileInput` components. If we have files that are on different disks, then they are read concurrently, but only one read from one disk is done at a time. After reading the file, its name and data are forwarded to the *input* queues of all the cruncher components to which the input component is currently linked. If a cruncher component connects to an input component after, or while reading some files, there is no guarantee that those files will be passed to that cruncher component.

In addition, the component actively works and keeps track of whether a new file has appeared, or whether any of the files already processed have been modified in the meantime. It detects file changes by checking the `last modified` file attribute. After the component has checked all the specified directories, it is locked for a fixed period, after which it checks again all the directories. The blocking time is set through the configuration file. The `FileInput` component may be paused, in which case no new scans can start until it is restarted. When a component restarts, it starts scanning immediately, regardless of how long it has been since the previous scan or pause.

### Cruncher Component

Cruncher components provide data processing. For each cruncher component, `arity` is defined when constructing, and this is the main attribute that distinguishes the individual cruncher components. Each cruncher component handles data over the following shared queues:
  * It has its own *input* queue, over which it is itself a *consumer*, and over which multiple input components can be a *producer*.
  * It is bound to arbitrarily many *output* blocking queues over which it is itself a *producer*, and each of these queues has one output component as *consumer*.

`CounterCruncher` component is tasked with counting how many times each word has appeared in the specified text. The input for this component is an object containing a `name` (absolute file path) and `text` (read from the file, and given as one String object). The word processing job is done within one `Thread Pool` that is intended for all cruncher components. 

Each single thread job within the `Thread Pool` is defined to run at approximately a similar length of text (`L`), regardless of how large the file came in at the input. When dividing a single file into parts, it is allowed to have one part that is significantly smaller than `L`. The other parts are approximately equal to `L`, where under "approximately" we allow the environment `L` to be less than one word in length. The parameter `L` is specified through the configuration file.

Lastly, the `arity` of the component dictates what we are actually counting. We define `"bag"` as an unordered set of words, where the words inside the bag do not have to be different. The words that occur side by side inside the text are contained together in a bag. The arity cruncher of the component defines the size of the bags to be counted. 
> If arity is 3, the cruncher component will produce distribution for bags of size 3 in the given text. Notice that in the case of arity 1, we calculate the distribution for individual words.

As soon as the counting for an input object starts, all output components are reported to have started the job, and allow them to display it as active. To the job name (filename) is added the suffix `"-arityX"`, where *X* is the arity for the cruncher component doing the job. As soon as the job is completed, the output components have the number of occurrences of all bags of this arity in this text.

### Output Component

Output components provide storage and display the finished processing in our system. Each output component has one *"output"* blocking queue over which it is the *consumer*, and over which multiple cruncher components can be *producer*.

`CacheOutput` component is implemented to store the calculation results in memory. The results are stored in the map format `[result name -> the number of occurrences of all bags and the arity in that file]`.
This component also provides *aggregation* of already calculated results, specifically unionization and summation. The aggregation itself is done by forming a union of all the listed results. If there are different impression numbers for the same word or bag, then these results should be summed up in the union.

### Input

  * Creating new `FileInput` components, each tied to a single disk.
  * Linking the `FileInput` component to the cruncher component.
  * Unlinking the `FileInput` component from the cruncher component.
  * Adding a directory to a `FileInput` component.
  * Deleting a directory from the `FileInput` component.
  * Starting / pausing the `FileInput` component.
  * Removing the `FileInput` Component.
  * View the current `FileInput` activity of the component.
  
### Cruncher
  
  * Creating new `CounterCruncher` components, each is given arity.
  * Deleting `CounterCruncher` Components. Upon deletion, the component is automatically unlinked from any input components to which it was linked.
  * View the items that the cruncher component is currently processing.
  * Currently all `CounterCruncher` components are linked to a single `CacheOutput` component, so there is no need to bind and unlink them through the GUI.
  
### Output

  * Currently there is only one `CacheOutput` component on the GUI.
  * List of results is shown, given as a list of file names. Active jobs (still processed within some `CounterCruncher` component) have a * before the name.
  * Retrieving results. The results are sorted descending by the number of occurrences of words in a separate thread, the progress of which is displayed by the progress bar component. Progress is refreshed at every `K` comparison within the sorting process, where `K` is read from the configuration file. After sorting is complete, the results are displayed as a *line chart* of the frequency for the first 100 words.
  * Starting an aggregation business. The user is asked for a unique sum name before starting the job.
  
## Configuration

The system is configured using the `app.properties` configuration file, which has the following parameters:

`#blocking time for the FileInput component, given in milliseconds`
<br />`file_input_sleep_time = 5000`
<br />`#list of disks for the FileInput component, separated by a character;`
<br />`disks = data / disk1 /; data / disk2 /`
<br />`#limit to split work in CounterCruncher component, given in characters`
<br />`counter_data_limit = 10000000`
<br />`#the number of comparisons after which the progress in sorting is refreshed`
<br />`sort_progress_limit = 10000`

All of these parameters are read once at application startup, and will not change during operation. The only way to change the values of these parameters is to stop the application completely and restart it.
