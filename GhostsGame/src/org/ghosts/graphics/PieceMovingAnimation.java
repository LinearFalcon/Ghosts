package org.ghosts.graphics;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PieceMovingAnimation extends Animation {
	FlowPanel panel;
	PopupPanel popupPanel = new PopupPanel(); 		// Used for moving animation
	Image start;
	int startX, startY;
	int endX, endY;
	Audio soundAtEnd;

	public PieceMovingAnimation(int startX, int startY, int endX, int endY, Image fromImg, //Image toImg,
			Audio sfx) {
		VerticalPanel vp = new VerticalPanel();
		this.start = fromImg;
        vp.add(start);
        this.popupPanel.setWidget(vp);
		this.startX = startX;
		this.startY = startY;
		popupPanel.setPopupPosition(startX, startY);
		popupPanel.show();

		this.endX = endX;
		this.endY = endY;
		
		soundAtEnd = sfx;
	}

	@Override
	protected void onUpdate(double progress) {
		double positionX = startX + (progress * (this.endX - this.startX));
        double positionY = startY + (progress * (this.endY - this.startY));
        popupPanel.setPopupPosition((int) positionX, (int) positionY);
	}

	@Override
	protected void onComplete() {
		super.onComplete();
        if(soundAtEnd != null)
        	soundAtEnd.play();
        popupPanel.hide();
	}

	
}