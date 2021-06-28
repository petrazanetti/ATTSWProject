package petra.ATTSWproject.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import petra.ATTSWproject.model.User;
import petra.ATTSWproject.repository.StudyRoomRepository;
import petra.ATTSWproject.view.StudyRoomView;

public class StudyRoomControllerTest {
	
	private StudyRoomController studyRoomController;
	
	@Mock
	private StudyRoomRepository studyRoomRepository;

	@Mock
	private StudyRoomView studyRoomView;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		studyRoomController = new StudyRoomController(studyRoomView,studyRoomRepository, 10)
;	}
	
	@Test
	public void testAddingNewUserWhenUserDoesNotAlreadyExist() {
		studyRoomController.setCurrentCapacity(0);
		User user = new User("1", "Klara");
		when(studyRoomRepository.findById("1")).thenReturn(null);
		studyRoomController.newUser(user);
		InOrder inOrder = inOrder(studyRoomRepository, studyRoomView);
		inOrder.verify(studyRoomRepository).save(user);
		inOrder.verify(studyRoomView).userAdded(user);
		assertEquals(1,studyRoomController.getCurrentCapacity(),0);
	}
	
	@Test
	public void testAddingNewUserWhenUserDoesAlreadyExist() {
		User existingUser = new User("1", "Klara");
		studyRoomController.setCurrentCapacity(1);
		User addedUser = new User("1", "Petra");
		when(studyRoomRepository.findById("1")).thenReturn(existingUser);
		studyRoomController.newUser(addedUser);
		verify(studyRoomView).showError("Student with id 1 already exists", existingUser);
		verifyNoMoreInteractions(ignoreStubs(studyRoomRepository));
		assertEquals(1,studyRoomController.getCurrentCapacity(),0);

	}
	
	@Test
	public void testDeletingUserWhenUserExists() {
		User user = new User("1", "Klara");
		studyRoomController.setCurrentCapacity(1);
		when(studyRoomRepository.findById("1")).thenReturn(user);
		studyRoomController.deleteUser(user);
		InOrder inOrder = inOrder(studyRoomRepository, studyRoomView);
		inOrder.verify(studyRoomRepository).delete("1");
		inOrder.verify(studyRoomView).userRemoved(user);
		assertEquals(0,studyRoomController.getCurrentCapacity(),0);
	}
	
	@Test
	public void testDeletingUserWhenUserDoesNotExist() {
		studyRoomController.setCurrentCapacity(5);
		User user = new User("1", "Klara");
		when(studyRoomRepository.findById("1")).thenReturn(null);
		studyRoomController.deleteUser(user);
		verify(studyRoomView).showError("Student with id 1 does not exist", user);
		verifyNoMoreInteractions(ignoreStubs(studyRoomRepository));
		assertEquals(5,studyRoomController.getCurrentCapacity(),0);
	}
	
	@Test
	public void testAddingNewUserWhenStudyRoomIsFull() {
		studyRoomController.setCurrentCapacity(10);
		User user = new User("1", "Klara");
		studyRoomController.newUser(user);
		verify(studyRoomView).showError("Study room is full");
		verifyNoMoreInteractions(studyRoomRepository);
		assertEquals(10,studyRoomController.getCurrentCapacity(),0);
	}
	

}
