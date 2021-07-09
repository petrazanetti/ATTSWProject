package petra.ATTSWproject.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import petra.ATTSWproject.controller.StudyRoomController;
import petra.ATTSWproject.model.User;
import petra.ATTSWproject.repository.mongo.StudyRoomMongoRepository;

public class SwingViewIT extends AssertJSwingJUnitTestCase{
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient client;
	private FrameFixture window;
	private StudyRoomController studyRoomController;
	private StudyRoomMongoRepository studyRoomMongoRepository;
	private StudyRoomSwingView studyRoomSwingView;

	public static final String USER_COLLECTION_NAME = "user";
	public static final String STUDY_ROOM_DB_NAME = "studyRoom";
	
	@Before
	public void onSetUp() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(),mongo.getFirstMappedPort()));
		studyRoomMongoRepository = new StudyRoomMongoRepository(client,STUDY_ROOM_DB_NAME,USER_COLLECTION_NAME);
		for (User student : studyRoomMongoRepository.findAll()) {
			studyRoomMongoRepository.delete(student.getId());
		}
		GuiActionRunner.execute(() -> {
			studyRoomSwingView = new StudyRoomSwingView();
			studyRoomController = new StudyRoomController(studyRoomSwingView, studyRoomMongoRepository,10);
			studyRoomSwingView.setStudyRoomController(studyRoomController);
			return studyRoomSwingView;
		});
		window = new FrameFixture(robot(), studyRoomSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		client.close();
	}
	
	@Test @GUITest
	public void testAllUsers() {
		User user1 = new User("1", "User1");
		User user2 = new User("2", "User2");
		studyRoomMongoRepository.save(user1);
		studyRoomMongoRepository.save(user2);
		GuiActionRunner.execute(() -> studyRoomController.allUsers());
		assertThat(window.list().contents()).containsExactly(user1.toString(), user2.toString());
	}

	@Test @GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("User");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).containsExactly(new User("1", "User").toString());
	}

	@Test @GUITest
	public void testAddButtonError() {
		User user = new User("1", "existingUser");
		studyRoomMongoRepository.save(user);
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("newUser");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).isEmpty();
		window.label("errorMessageLabel").requireText("User with id 1 already exists");
	}

	@Test @GUITest
	public void testDeleteButtonSuccess() {
		GuiActionRunner.execute(() -> studyRoomController.newUser(new User("1", "User")));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete")).click();
		assertThat(window.list().contents()).isEmpty();
	}

	@Test @GUITest
	public void testDeleteButtonError() {
		User user = new User("1", "User");
		GuiActionRunner.execute(() -> studyRoomSwingView.getListUsersModel().addElement(user));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete")).click();
		assertThat(window.list().contents()).containsExactly(user.toString());
		window.label("errorMessageLabel").requireText("User with id " + user.getId() + " does not exist");
	}

}
