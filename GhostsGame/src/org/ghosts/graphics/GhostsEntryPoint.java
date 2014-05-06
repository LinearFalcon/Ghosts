package org.ghosts.graphics;

import org.game_api.GameApi;
import org.game_api.GameApi.*;
import org.ghosts.client.GhostsLogic;
import org.ghosts.client.GhostsPresenter;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.theme.base.ButtonCss;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */


public class GhostsEntryPoint implements EntryPoint {
	Container container;
	GhostsPresenter ghostsPresenter;

	@Override
	public void onModuleLoad() {
		Game game = new Game() {
			@Override
			public void sendVerifyMove(VerifyMove verifyMove) {
				container.sendVerifyMoveDone(new GhostsLogic().verify(verifyMove));
			}

			@Override
			public void sendUpdateUI(UpdateUI updateUI) {
				ghostsPresenter.updateUI(updateUI);
			}
		};
		container = new ContainerConnector(game);
		GhostsGraphics ghostsGraphics = new GhostsGraphics();
		ghostsPresenter = new GhostsPresenter(ghostsGraphics, container);
		
		RootPanel.get("mainDiv").add(ghostsGraphics);
		container.sendGameReady();
	}

}

/*
public class GhostsEntryPoint implements EntryPoint {
	IteratingPlayerContainer container;
	GhostsPresenter ghostsPresenter;

	@Override
	public void onModuleLoad() {
		Game game = new Game() {
			@Override
			public void sendVerifyMove(VerifyMove verifyMove) {
				container.sendVerifyMoveDone(new GhostsLogic().verify(verifyMove));
			}

			@Override
			public void sendUpdateUI(UpdateUI updateUI) {
				ghostsPresenter.updateUI(updateUI);
			}
		};
		container = new IteratingPlayerContainer(game, 2);
		GhostsGraphics ghostsGraphics = new GhostsGraphics();
		ghostsPresenter = new GhostsPresenter(ghostsGraphics, container);
		final ListBox playerSelect = new ListBox();
		playerSelect.addItem("WhitePlayer");
		playerSelect.addItem("BlackPlayer");
		playerSelect.addItem("Viewer");
		playerSelect.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int selectedIndex = playerSelect.getSelectedIndex();
				String playerId = selectedIndex == 2 ? GameApi.VIEWER_ID
						: container.getPlayerIds().get(selectedIndex);
				container.updateUi(playerId);
			}
		});
		playerSelect.setSize("200px", "100px");
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(ghostsGraphics);
		flowPanel.add(playerSelect);
		RootPanel.get("mainDiv").add(flowPanel);
		container.sendGameReady();
		container.updateUi(container.getPlayerIds().get(0));
	}

}
*/
/*
// Test entry point without emulator, thanks for Youlong Li and Lisa Luo's code
public class GhostsEntryPoint implements EntryPoint {
	IteratingPlayerContainer container;
	GhostsPresenter ghostsPresenter;

	@Override
	public void onModuleLoad() {
		Game game = new Game() {
			@Override
			public void sendVerifyMove(VerifyMove verifyMove) {
				container.sendVerifyMoveDone(new GhostsLogic().verify(verifyMove));
			}

			@Override
			public void sendUpdateUI(UpdateUI updateUI) {
				ghostsPresenter.updateUI(updateUI);
			}
		};
		container = new IteratingPlayerContainer(game, 2);
		GhostsGraphics ghostsGraphics = new GhostsGraphics();
		ghostsPresenter = new GhostsPresenter(ghostsGraphics, container);
		
		final HorizontalPanel buttonGroup = new HorizontalPanel();
		final ButtonCss buttonCss = MGWTStyle.getTheme().getMGWTClientBundle().getButtonCss();
	    final Button whitePlayer = new Button("White player");
	    final Button blackPlayer = new Button("Black player");
	    final Button viewer = new Button("Viewer"); 
	    whitePlayer.setSmall(true);
	    blackPlayer.setSmall(true);
	    viewer.setSmall(true);
	    
	    whitePlayer.addTapHandler(new TapHandler() {
	      @Override
	      public void onTap(TapEvent event) {
	        container.updateUi(container.getPlayerIds().get(0));
	        whitePlayer.addStyleName(buttonCss.active());
	        blackPlayer.removeStyleName(buttonCss.active());
	        viewer.removeStyleName(buttonCss.active());
	      }                    
	    });
	    
	    blackPlayer.addTapHandler(new TapHandler() {
	      @Override
	      public void onTap(TapEvent event) {
	        container.updateUi(container.getPlayerIds().get(1));
	        blackPlayer.addStyleName(buttonCss.active());
	        whitePlayer.removeStyleName(buttonCss.active());
	        viewer.removeStyleName(buttonCss.active());
	      }                    
	    });
	    
	    viewer.addTapHandler(new TapHandler() {
	      @Override
	      public void onTap(TapEvent event) {
	        container.updateUi(GameApi.VIEWER_ID);
	        viewer.addStyleName(buttonCss.active());
	        whitePlayer.removeStyleName(buttonCss.active());
	        blackPlayer.removeStyleName(buttonCss.active());
	      }                    
	    }); 
	    
	    buttonGroup.add(whitePlayer);
	    buttonGroup.add(blackPlayer);
	    buttonGroup.add(viewer);
	   
	    FlowPanel flowPanel = new FlowPanel();
	    flowPanel.add(ghostsGraphics);
	    flowPanel.add(buttonGroup);
		
		
		RootPanel.get("mainDiv").add(flowPanel);
		container.sendGameReady();
		container.updateUi(container.getPlayerIds().get(0));
	}

}
*/