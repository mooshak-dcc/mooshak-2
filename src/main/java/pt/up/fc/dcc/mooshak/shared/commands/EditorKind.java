package pt.up.fc.dcc.mooshak.shared.commands;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum EditorKind implements IsSerializable {

	CODE, DIAGRAM, GAME, QUIZ, FILL_IN_GAPS, SORT_BLOCKS, SPOT_BUG;
}
