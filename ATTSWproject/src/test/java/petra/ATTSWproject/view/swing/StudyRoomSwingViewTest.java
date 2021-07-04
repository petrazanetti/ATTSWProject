package petra.ATTSWproject.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import petra.ATTSWproject.controller.StudyRoomController;
import petra.ATTSWproject.model.User;

@RunWith(GUITestRunner.class)
public class StudyRoomSwingViewTest extends AssertJSwingJUnitTestCase{

	private FrameFixture window;
	
	private StudyRoomSwingView studyRoomSwingView;
	
	@Mock
	private StudyRoomController studyRoomController;
	
	private AutoCloseable closeable;

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			studyRoomSwingView = new StudyRoomSwingView();
			studyRoomSwingView.setStudyRoomController(studyRoomController);
			return studyRoomSwingView;
		});
		window = new FrameFixture(robot(), studyRoomSwingView);
		window.show(); 
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}
	
	@Test @GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("id"));
		window.textBox("idTextBox").requireEnabled();
		window.label(JLabelMatcher.withText("name"));
		window.textBox("nameTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
		window.list("usersList");
		window.label("errorMessageLabelFullRoom").requireText(" ");
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testWhenIdAndNameAreNotEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("Petra");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}
	
	@Test
	public void testWhenEitherIdOrNameAreBlankThenAddButtonShouldBeDisabled() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		
		window.textBox("idTextBox").setText("");
		window.textBox("nameTextBox").setText("");
		
		window.textBox("idTextBox").enterText("");
		window.textBox("nameTextBox").enterText("Petra");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}
	
	@Test
	public void testAddButtonShouldDelegateToStudyRoomControllerNewUser() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("Petra");
		window.button(JButtonMatcher.withText("Add")).click();
		verify(studyRoomController).newUser(new User("1", "Petra"));
	}
	
	@Test
	public void testDeleteButtonShouldBeEnabledOnlyWhenAUserIsSelected() {
		GuiActionRunner.execute(() -> studyRoomSwingView.getListUsersModel().addElement(new User("1", "Petra")));
		window.list("usersList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete"));
		deleteButton.requireEnabled();
		window.list("usersList").clearSelection();
		deleteButton.requireDisabled();
	}
	
	@Test
	public void testDeleteButtonShouldDelegateToStudyRoomControllerDeleteUser() {
		User user1 = new User("1", "user1");
		User user2 = new User("2", "user2");
		GuiActionRunner.execute(
			() -> {
				DefaultListModel<User> listUsersModel = studyRoomSwingView.getListUsersModel();
				listUsersModel.addElement(user1);
				listUsersModel.addElement(user2);
			}
		);
		window.list("usersList").selectItem(1);
		window.button(JButtonMatcher.withText("Delete")).click();
		verify(studyRoomController).deleteUser(user2);
	}
	
	@Test
	public void testsShowAllStudentsShouldAddStudentDescriptionsToTheList() {
		User user1 = new User("1", "user1");
		User user2 = new User("2", "user2");
		GuiActionRunner.execute(
			() -> studyRoomSwingView.showAllUsers(
					Arrays.asList(user1, user2))
		);
		String[] listContents = window.list().contents();
		assertThat(listContents)
			.containsExactly(user1.toString(), user2.toString());
	}
	
	@Test
	public void testShowErrorWhenStudentCanNotBeDeletedShouldShowTheMessageInTheErrorLabel() {
		User user = new User("1", "user1");
		GuiActionRunner.execute(
			() -> studyRoomSwingView.showError("error message", user)
		);
		//window.label("errorMessageLabel").requireText("error message: " + user);
		window.label("errorMessageLabel").requireText("error message");

	}
	
	@Test
	public void testStudentAddedShouldAddTheStudentToTheListAndResetTheErrorLabel() {
		User user = new User("1", "test1");
		GuiActionRunner.execute(
				() ->
				studyRoomSwingView.userAdded(new User("1", "test1"))
				);
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(user.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testStudentRemovedShouldRemoveTheStudentFromTheListAndResetTheErrorLabels() {
		// setup
		User user1 = new User("1", "test1");
		User user2 = new User("2", "test2");
		GuiActionRunner.execute(
			() -> {
				DefaultListModel<User> listStudentsModel = studyRoomSwingView.getListUsersModel();
				listStudentsModel.addElement(user1);
				listStudentsModel.addElement(user2);
			}
		);
		// execute
		GuiActionRunner.execute(
			() ->
			studyRoomSwingView.userRemoved(new User("1", "test1"))
		);
		// verify
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(user2.toString());
		window.label("errorMessageLabel").requireText(" ");
		window.label("errorMessageLabelFullRoom").requireText(" ");
	}
	
	@Test
	public void testShowErrorWhenRoomIsFullShouldShowTheMessageInTheErrorLabel() {
		GuiActionRunner.execute(
			() -> studyRoomSwingView.showError("Study room is full")
		);
		window.label("errorMessageLabelFullRoom").requireText("Study room is full");
	}
	

	  @Test 
	  public void testWhenStudyRoomIsFullThenAddButtonShouldBeDisabledWhenAddingNewUser() {
		  GuiActionRunner.execute( () -> studyRoomSwingView.showError("Study room is full") );
		  window.textBox("idTextBox").enterText("1");
		  window.textBox("nameTextBox").enterText("Petra");
		  window.button(JButtonMatcher.withText("Add")).requireDisabled();
	  }

}
