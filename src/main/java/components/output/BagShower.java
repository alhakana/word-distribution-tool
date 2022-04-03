package components.output;

import components.Output;
import components.Utils;
import mvc.model.FileOutput;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BagShower implements Runnable {

    private Output output;
    private CacheOutputComp cacheOutputComp;

    public BagShower(Output output, CacheOutputComp cacheOutputCompMap) {
        this.output = output;
        this.cacheOutputComp = cacheOutputCompMap;
    }

    @Override
    public void run() {
        Map<String, Future<Map<String, Integer>>> result = cacheOutputComp.getResult();
        String name = output.getName();
        result.put(name, output.getBagOfWords());

        FileOutput fileOutput = new FileOutput(name);
        Utils.updateList(cacheOutputComp.getObservableList(), fileOutput);

        try {
            Map<String, Integer> map = output.getBagOfWords().get();
            System.gc();
            Utils.removeAndUpdateList(cacheOutputComp.getObservableList(), fileOutput);

            System.out.println("BAG SHOWER DONE " + map.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
