package petra.ATTSWproject;

public interface StudyRoomRepository {

	User findById(String string);

	void save(User user);

	void delete(String string);

	int size();

}
