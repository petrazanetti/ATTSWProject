package petra.ATTSWproject.app;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import petra.ATTSWproject.controller.StudyRoomController;
import petra.ATTSWproject.repository.StudyRoomMongoRepository;
import petra.ATTSWproject.view.swing.StudyRoomSwingView;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class StudyRoomApp implements Callable<Void>{

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;
	
	@Option(names = { "--room-capacity" }, description = "Maximum capacity of the study room")
	private int roomCapacity = 10;
	
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "studyRoom";

	@Option(names = { "--db-collection" }, description = "Collection name")
	private String collectionName = "user";

	public static void main(String[] args) {
		new CommandLine(new StudyRoomApp()).execute(args);
	}
	
	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				StudyRoomMongoRepository studyRoomMongoRepository = new StudyRoomMongoRepository(
						new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, collectionName);
				StudyRoomSwingView studyRoomSwingView = new StudyRoomSwingView();
				StudyRoomController studyRoomController = new StudyRoomController(studyRoomSwingView, studyRoomMongoRepository, roomCapacity);
				studyRoomSwingView.setStudyRoomController(studyRoomController);
				studyRoomSwingView.setVisible(true);
				studyRoomController.allUsers();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}

}
