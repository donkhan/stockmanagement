package thread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkhan on 07/07/16.
 */
public class TestParallel {


    public static void main(String args[]){
        String stocks[] = new String[]{"http://www.moneycontrol.com/india/stockpricequote/steel-large/steelauthorityindia/SAI",
                "http://www.moneycontrol.com/india/stockpricequote/refineries/indianoilcorporation/IOC",
                "http://www.moneycontrol.com/india/stockpricequote/infrastructure-general/gmrinfrastructure/GI27"};
        List<StockThread> list = new ArrayList<StockThread>();
        for(int i = 0;i<stocks.length;i++) {
            StockThread st = new StockThread(stocks[i],"");
            st.start();
        }

        for(StockThread st : list){
            try {
                st.join();
            }catch(Exception e){
                e.printStackTrace();
            }
            System.out.println(st.getStockPrice());

        }
    }

}
