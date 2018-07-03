package bsbll.config;

import java.io.File;

import bsbll.Year;
import bsbll.game.params.GamePlayParams;
import bsbll.game.params.GamePlayParamsFactory;
import p3.Persister;
import p3.XmlPersisterStore;

final class GamePlayParamsFileWriter {
    
    public static void write(Year year) throws Exception {
        GamePlayParams params = GamePlayParamsFactory.retrosheet(year).createParams();
        write(params, year);
    }
    
    public static void write(GamePlayParams params, Year year) throws Exception {
        Persister p = new Persister();
        params.store(p);
        
        XmlPersisterStore xml = XmlPersisterStore.newInstance().store(p);
        File output = GamePlayParamsConfig.getStorageOutput(year);
        
        xml.writeTo(output);
    }

    public static void main(String[] args) throws Exception {
        // Creating the 1923 GamePlayParams. Retrosheet does not have play-by-play files
        // for 1923, so using the 1925 data instead.
        GamePlayParams params = GamePlayParamsFactory.retrosheet(Year.of(1925)).createParams();
        write(params, Year.of(1923));
    }
}
