package petra.ATTSWproject.view;

import java.util.List;


import petra.ATTSWproject.model.User;

public interface StudyRoomView {
	
	void showAllUsers(List<User> users);

	void userAdded(User user);

	void showError(String string, User user);
	
	void showError(String string);

	void userRemoved(User user);

	void showDeletingError(String message, User user);

}
