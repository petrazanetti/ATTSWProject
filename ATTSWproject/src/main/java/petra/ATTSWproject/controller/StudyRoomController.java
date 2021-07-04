package petra.ATTSWproject.controller;

import petra.ATTSWproject.model.User;
import petra.ATTSWproject.repository.StudyRoomRepository;
import petra.ATTSWproject.view.StudyRoomView;

public class StudyRoomController {
	
	private StudyRoomView studyRoomView;
	private StudyRoomRepository studyRoomRepository;
	private int maxCapacity;
	private int currentCapacity;

	
	public int getCurrentCapacity() {
		return currentCapacity;
	}
	
	public void setCurrentCapacity(int capacity) {
		currentCapacity = capacity;
	}
	
	public StudyRoomController(StudyRoomView studyRoomView, StudyRoomRepository studyRoomRepository, int maxCapacity) {
		this.studyRoomView = studyRoomView;
		this.studyRoomRepository = studyRoomRepository;
		this.maxCapacity = maxCapacity;
		this.currentCapacity = 0;
	}
	
	public void allUsers() {
		studyRoomView.showAllUsers(studyRoomRepository.findAll());
	}

	public void newUser(User user) {
		if(currentCapacity==maxCapacity) {
			studyRoomView.showError("Study room is full");
			return;
		}
		User existingUser = studyRoomRepository.findById(user.getId());
		if(existingUser != null) {
			studyRoomView.showError("User with id " + existingUser.getId() + " already exists", existingUser);
			return;
		}
		studyRoomRepository.save(user);
		studyRoomView.userAdded(user);
		currentCapacity+=1;
		
	}

	public void deleteUser(User user) {
		User existingUser = studyRoomRepository.findById(user.getId());
		if(existingUser == null) {
			studyRoomView.showError("User with id " + user.getId() + " does not exist", user);
			return;
		}
		studyRoomRepository.delete(user.getId());
		studyRoomView.userRemoved(user);
		currentCapacity-=1;

	}





}
