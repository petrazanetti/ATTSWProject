package petra.ATTSWproject;

public interface StudyRoomView {

	void userAdded(User user);

	void showError(String string, User existingUser);

	void userRemoved(User user);

}
