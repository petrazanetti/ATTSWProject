package petra.ATTSWproject.mongo.repository;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.*;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import petra.ATTSWproject.model.User;
import petra.ATTSWproject.repository.StudyRoomMongoRepository;

public class StudyRoomMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	private MongoClient client;
	private StudyRoomMongoRepository studyRoomRepository;
	private MongoCollection<Document> userCollection;
	
	public static final String USER_COLLECTION_NAME = "user";
	public static final String STUDY_ROOM_DB_NAME = "studyRoom";
	
	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}
	
	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}
	
	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));
		studyRoomRepository = new StudyRoomMongoRepository(client,STUDY_ROOM_DB_NAME,USER_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase("studyRoom");
		database.drop();
		userCollection = database.getCollection("user");
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	private void addTestUserToDatabase(String id, String name) {
		Document user = new Document().append("id", id).append("name", name);
		userCollection.insertOne(user);
	}
	
	private List<User> readAllUsersFromDatabase() {
		return StreamSupport.
			stream(userCollection.find().spliterator(), false)
				.map(d -> new User(""+d.get("id"), ""+d.get("name")))
				.collect(Collectors.toList());
	}
	
	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(studyRoomRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestUserToDatabase("1", "test1");
		addTestUserToDatabase("2", "test2");
		assertThat(studyRoomRepository.findAll()).containsExactly(new User("1", "test1"),new User("2", "test2"));
	}
	
	@Test
	public void testFindByIdWhenUserIsNotFound() {
		assertThat(studyRoomRepository.findById("1")).isNull();
	}
	
	@Test
	public void testFindByIdWhenUserIsFound() {
		addTestUserToDatabase("1", "User");
		assertThat(studyRoomRepository.findById("1")).isEqualTo(new User("1","User"));
	}
	
	@Test
	public void testSaveUser() {
		User user = new User("1", "User");
		studyRoomRepository.save(user);
		assertThat(readAllUsersFromDatabase()).containsExactly(user);
	}
	
	@Test
	public void testDeleteUser() {
		addTestUserToDatabase("1", "User");
		studyRoomRepository.delete("1");
		assertThat(readAllUsersFromDatabase()).isEmpty();
	}
	
	
}
