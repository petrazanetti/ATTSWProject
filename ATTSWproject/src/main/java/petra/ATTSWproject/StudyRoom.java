package petra.ATTSWproject;

public class StudyRoom {
	
	private StudyRoomView studyRoomView;
	private StudyRoomRepository studyRoomRepository;
	private int maxCapacity;
	
	public int getMaxCapacity() {
		return maxCapacity;
	}
	
	public StudyRoom(StudyRoomView studyRoomView, StudyRoomRepository studyRoomRepository, int maxCapacity) {
		this.studyRoomView = studyRoomView;
		this.studyRoomRepository = studyRoomRepository;
		this.maxCapacity = maxCapacity;
	}

	public void newUser(User user) {
		if(studyRoomRepository.size()==maxCapacity) {
			throw new IllegalArgumentException("Study room is full");
		}
		User existingUser = studyRoomRepository.findById(user.getId());
		if(existingUser != null) {
			throw new IllegalArgumentException("There already exists user with id " + existingUser.getId());
		}
		studyRoomRepository.save(user);
		studyRoomView.userAdded(user);
	}

	public void deleteUser(User user) {
		User existingUser = studyRoomRepository.findById(user.getId());
		if(existingUser == null) {
			throw new IllegalArgumentException("Student with id " + user.getId() + " does not exist");
		}
		studyRoomRepository.delete(user.getId());
		studyRoomView.userRemoved(user);		
	}



}
