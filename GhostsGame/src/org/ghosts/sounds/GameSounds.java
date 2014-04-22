package org.ghosts.sounds;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface GameSounds extends ClientBundle {

    @Source("org/ghosts/sounds/pieceCaptured.mp3")
    DataResource pieceCapturedMp3();
    
    @Source("org/ghosts/sounds/pieceCaptured.wav")
    DataResource pieceCapturedWav();

    @Source("org/ghosts/sounds/pieceDown.mp3")
    DataResource pieceDownMp3();
    
    @Source("org/ghosts/sounds/pieceDown.wav")
    DataResource pieceDownWav();
    
}
