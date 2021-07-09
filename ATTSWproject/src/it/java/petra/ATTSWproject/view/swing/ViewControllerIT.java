package petra.ATTSWproject.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import petra.ATTSWproject.controller.StudyRoomController;
import petra.ATTSWproject.model.User;
import petra.ATTSWproject.repository.mongo.StudyRoomMongoRepository;

public class ViewControllerIT extends AssertJSwingJUnitTestCase{
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient client;
	private FrameFixture window;
	private StudyRoomController studyRoomController;
	private StudyRoomMongoRepository studyRoomMongoRepository;
	
	public static final String USER_COLLECTION_NAME = "user";
	public static final String STUDY_ROOM_DB_NAME = "studyRoom";

	@Before
	public void onSetUp() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(),mongo.getFirstMappedPort()));
		studyRoomMongoRepository = new StudyRoomMongoRepository(client,STUDY_ROOM_DB_NAME,USER_COLLECTION_NAME);
		for (User student : studyRoomMongoRepository.findAll()) {
			studyRoomMongoRepository.delete(student.getId());
		}
		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			StudyRoomSwingView studyRoomSwingView = new StudyRoomSwingView();
			studyRoomController = new StudyRoomController(studyRoomSwingView, studyRoomMongoRepository,10);
			studyRoomSwingView.setStudyRoomController(studyRoomController);
			return studyRoomSwingView;
		}));
		window.show();
	}
	
	@After
	public void onTearDown() {
		client.close();
	}
	
	@Test
	public void testAddUser() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("User");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(studyRoomMongoRepository.findById("1")).isEqualTo(new User("1", "User"));
	}

	@Test
	public void testDeleteUser() {
		studyRoomMongoRepository.save(new User("1", "User"));
		GuiActionRunner.execute(() -> studyRoomController.allUsers());
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete")).click();
		assertThat(studyRoomMongoRepository.findById("1")).isNull();
	}
}
