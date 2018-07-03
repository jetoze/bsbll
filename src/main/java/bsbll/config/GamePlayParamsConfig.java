package bsbll.config;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import bsbll.Year;
import bsbll.game.params.GamePlayParams;
import p3.Persister;
import p3.XmlPersisterStore;

public final class GamePlayParamsConfig {

    private GamePlayParamsConfig() {/**/}

    private static String getStorageResourcePath(Year year) {
        return String.format("/config/%s/GamePlayParams.xml", year);
    }
    
    static File getStorageOutput(Year year) {
        return new File("/Users/torgil/coding/repos/bsbll/src/main/resources" + getStorageResourcePath(year));
    }
    
    private static InputStream getStorageResource(Year year) {
        String path = getStorageResourcePath(year);
        return GamePlayParams.class.getResourceAsStream(path);
    }
    
    public static GamePlayParams readParams(Year year) {
        requireNonNull(year);
        try (InputStream is = getStorageResource(year)) {
            Persister p = XmlPersisterStore.load(is);
            return GamePlayParams.restoreFrom(p);
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
