package petra.ATTSWproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StudyRoomTest {
	
	private StudyRoom studyRoom;
	
	@Mock
	private StudyRoomRepository studyRoomRepository;

	@Mock
	private StudyRoomView studyRoomView;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		studyRoom = new StudyRoom(studyRoomView,studyRoomRepository, 10)
;	}
	
	@Test
	public void testAddingNewUserWhenUserDoesNotAlreadyExist() {
		User user = new User("1", "Klara");
		when(studyRoomRepository.findById("1")).thenReturn(null);
		studyRoom.newUser(user);
		InOrder inOrder = inOrder(studyRoomRepository, studyRoomView);
		inOrder.verify(studyRoomRepository).save(user);
		inOrder.verify(studyRoomView).userAdded(user);
	}
	
	@Test
	public void testAddingNewUserWhenUserDoesAlreadyExist() {
		User existingUser = new User("1", "Klara");
		User addedUser = new User("1", "Petra");
		when(studyRoomRepository.findById("1")).thenReturn(existingUser);
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> studyRoom.newUser(addedUser));
		assertEquals("There already exists user with id 1", e.getMessage());
	}
	
	@Test
	public void testDeletingUserWhenUserExists() {
		User user = new User("1", "Klara");
		when(studyRoomRepository.findById("1")).thenReturn(user);
		studyRoom.deleteUser(user);
		InOrder inOrder = inOrder(studyRoomRepository, studyRoomView);
		inOrder.verify(studyRoomRepository).delete("1");
		inOrder.verify(studyRoomView).userRemoved(user);
	}
	
	@Test
	public void testDeletingUserWhenUserDoesNotExist() {
		User user = new User("1", "Klara");
		when(studyRoomRepository.findById("1")).thenReturn(null);
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> studyRoom.deleteUser(user));
		assertEquals("Student with id 1 does not exist", e.getMessage());
	}
	
	@Test
	public void testAddingNewUserWhenStudyRoomIsFull() {
		User user = new User("1", "Klara");
		when(studyRoomRepository.size()).thenReturn(studyRoom.getMaxCapacity());
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> studyRoom.newUser(user));
		assertEquals("Study room is full", e.getMessage());
	}
	

}
