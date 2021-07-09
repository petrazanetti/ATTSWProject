package petra.ATTSWproject.repository;

import java.util.List;


import petra.ATTSWproject.model.User;

public interface StudyRoomRepository {
	
	List<User> findAll();

	User findById(String string);

	void save(User user);

	void delete(String string);

}
