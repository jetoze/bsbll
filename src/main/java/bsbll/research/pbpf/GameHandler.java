package bsbll.research.pbpf;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import bsbll.research.EventField;
import bsbll.research.PlayOutcome;
import bsbll.research.pbpf.PlayByPlayFile.Inning;

public abstract class GameHandler {
    private PlayByPlayFile currentFile;
    private String currentGameId;
    private Inning currentInning;
    
    public void onStartGame(String id) {
        /**/
    }
    
    public void onEndOfInning(Inning inning,
                              ImmutableList<EventField> fields,
                              ImmutableList<PlayOutcome> plays) {
        /**/
    }
    
    public void onEndGame(String id) {
        /**/
    }
    
    protected final PlayByPlayFile getCurrentFile() {
        checkState(currentFile != null, "Not parsing a file");
        return currentFile;
    }
    
    protected final String getCurrentGameId() {
        checkState(currentGameId != null, "Not parsing a game");
        return currentGameId;
    }
    
    public final void parse(PlayByPlayFile file) {
        requireNonNull(file);
        parseAndRoute(r -> parseImpl(file, r));
    }
    
    private void parseImpl(PlayByPlayFile file, EventRouter router) {
        currentFile = file;
        file.parse(router);
        currentFile = null;
    }
    
    public final void parseAll(File folder) {
        requireNonNull(folder);
        parseAndRoute(r -> PlayByPlayFile.stream(folder).forEach(f -> parseImpl(f, r)));
    }

    private void parseAndRoute(Consumer<EventRouter> f) {
        EventRouter router = new EventRouter();
        f.accept(router);
        if (currentGameId != null) {
            router.endPreviousGame();
        }
    }
    
    private class EventRouter implements PlayByPlayFile.Callback {
        private ImmutableList.Builder<EventField> fields;
        private ImmutableList.Builder<PlayOutcome> plays;
        
        @Override
        public void onStartGame(String id) {
            if (currentGameId != null) {
                endPreviousGame();
            }
            currentGameId = id;
            GameHandler.this.onStartGame(id);
            plays = null;
        }
        
        public void endPreviousGame() {
            assert currentInning != null;
            assert fields != null;
            assert plays != null;
            GameHandler.this.onEndOfInning(currentInning, fields.build(), plays.build());
            GameHandler.this.onEndGame(currentGameId);
            currentInning = null;
            currentGameId = null;
            plays = null;
        }

        @Override
        public void onStartInning(Inning inning) {
            currentInning = inning;
            fields = ImmutableList.builder();
            plays = ImmutableList.builder();
        }

        @Override
        public void onEvent(EventField field, PlayOutcome outcome) {
            fields.add(field);
            plays.add(outcome);
        }
    }
}
