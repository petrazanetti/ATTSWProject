package petra.ATTSWproject.view;

import petra.ATTSWproject.model.User;

public interface StudyRoomView {

	void userAdded(User user);

	void showError(String string, User user);
	
	void showError(String string);

	void userRemoved(User user);

}
