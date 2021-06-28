package petra.ATTSWproject.repository;

import petra.ATTSWproject.model.User;

public interface StudyRoomRepository {

	User findById(String string);

	void save(User user);

	void delete(String string);

}
