package com.liangfang.ghosts.graphics;

import com.liangfang.ghosts.client.GhostsLogic;
import com.liangfang.ghosts.client.GameApi.IteratingPlayerContainer;
import com.liangfang.ghosts.client.GhostsPresenter;
import com.liangfang.ghosts.client.GameApi.Game;
import com.liangfang.ghosts.client.GameApi;
import com.liangfang.ghosts.client.GameApi.VerifyMove;
import com.liangfang.ghosts.client.GameApi.UpdateUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

public class GhostsEntryPoint implements EntryPoint {
	IteratingPlayerContainer container;
	GhostsPresenter ghostsPresenter;

	@Override
	public void onModuleLoad() {
		Game game = new Game() {
			@Override
			public void sendVerifyMove(VerifyMove verifyMove) {
				container.sendVerifyMoveDone(new GhostsLogic()
						.verify(verifyMove));
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
				int playerId = selectedIndex == 2 ? GameApi.VIEWER_ID
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
