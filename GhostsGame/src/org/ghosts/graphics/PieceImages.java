package org.ghosts.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PieceImages extends ClientBundle {
	@Source("images/pieces/WHITEBACK.png")
	ImageResource whiteback();
	
	@Source("images/pieces/BLACKBACK.png")
	ImageResource blackback();
	
	@Source("images/pieces/GOOD.png")
	ImageResource good();
	
	@Source("images/pieces/EVIL.png")
	ImageResource evil();
	
	@Source("images/pieces/BACKROUND.png")
	ImageResource backround();
	
	@Source("images/pieces/LEFTEXIT.png")
	ImageResource leftexit();
	
	@Source("images/pieces/RIGHTEXIT.png")
	ImageResource rightexit();
	
	@Source("images/pieces/DEPLOYPLACE.png")
	ImageResource deployplace();
}
