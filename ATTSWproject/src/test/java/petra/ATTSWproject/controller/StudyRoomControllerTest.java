package petra.ATTSWproject.controller;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
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
	
	private AutoCloseable closeable;
	
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		studyRoomController = new StudyRoomController(studyRoomView,studyRoomRepository, 10);
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void testAllUsers() {
		List<User> users = asList(new User("1", "User"));
		when(studyRoomRepository.findAll()).thenReturn(users);
		studyRoomController.allUsers();
		verify(studyRoomView).showAllUsers(users);
	}
	
	@Test
	public void testAddingNewUserWhenUserDoesNotAlreadyExist() {
		studyRoomController.setCurrentCapacity(0);
		User user = new User("1", "User");
		when(studyRoomRepository.findById("1")).thenReturn(null);
		studyRoomController.newUser(user);
		InOrder inOrder = inOrder(studyRoomRepository, studyRoomView);
		inOrder.verify(studyRoomRepository).save(user);
		inOrder.verify(studyRoomView).userAdded(user);
		assertEquals(1,studyRoomController.getCurrentCapacity(),0);
	}
	
	@Test
	public void testAddingNewUserWhenUserDoesAlreadyExist() {
		User existingUser = new User("1", "User1");
		studyRoomController.setCurrentCapacity(1);
		User addedUser = new User("1", "User2");
		when(studyRoomRepository.findById("1")).thenReturn(existingUser);
		studyRoomController.newUser(addedUser);
		verify(studyRoomView).showError("User with id 1 already exists", existingUser);
		verifyNoMoreInteractions(ignoreStubs(studyRoomRepository));
		assertEquals(1,studyRoomController.getCurrentCapacity(),0);

	}
	
	@Test
	public void testDeletingUserWhenUserExists() {
		User user = new User("1", "User");
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
		User user = new User("1", "User");
		when(studyRoomRepository.findById("1")).thenReturn(null);
		studyRoomController.deleteUser(user);
		verify(studyRoomView).showError("User with id 1 does not exist", user);
		verifyNoMoreInteractions(ignoreStubs(studyRoomRepository));
		assertEquals(5,studyRoomController.getCurrentCapacity(),0);
	}
	
	@Test
	public void testAddingNewUserWhenStudyRoomIsFull() {
		studyRoomController.setCurrentCapacity(10);
		User user = new User("1", "User");
		studyRoomController.newUser(user);
		verify(studyRoomView).showError("Study room is full");
		verifyNoMoreInteractions(studyRoomRepository);
		assertEquals(10,studyRoomController.getCurrentCapacity(),0);
	}
	

}
