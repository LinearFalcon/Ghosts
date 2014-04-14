package org.ghosts.graphics;

import org.game_api.GameApi;
import org.game_api.GameApi.*;
import org.ghosts.client.GhostsLogic;
import org.ghosts.client.GhostsPresenter;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
/*
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
*/

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
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(ghostsGraphics);
		flowPanel.add(playerSelect);
		RootPanel.get("mainDiv").add(flowPanel);
		container.sendGameReady();
		container.updateUi(container.getPlayerIds().get(0));
	}

}
