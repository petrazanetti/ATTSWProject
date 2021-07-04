package petra.ATTSWproject.mongo.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import petra.ATTSWproject.model.User;
import petra.ATTSWproject.repository.StudyRoomMongoRepository;
import static petra.ATTSWproject.repository.StudyRoomMongoRepository.USER_COLLECTION_NAME;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static petra.ATTSWproject.repository.StudyRoomMongoRepository.STUDY_ROOM_DB_NAME;

public class StudyRoomMongoRepositoryIT {
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient client;
	private StudyRoomMongoRepository studyRoomMongoRepository;
	private MongoCollection<Document> userCollection;
	
	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(),mongo.getFirstMappedPort()));
		studyRoomMongoRepository = new StudyRoomMongoRepository(client);
		MongoDatabase database = client.getDatabase(STUDY_ROOM_DB_NAME);
		database.drop();
		userCollection = database.getCollection(USER_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void testFindAll() {
		addTestUserToDatabase("1", "test1");
		addTestUserToDatabase("2", "test2");
		assertThat(studyRoomMongoRepository.findAll())
			.containsExactly(new User("1", "test1"),new User("2", "test2"));
	}
	
	@Test
	public void testFindById() {
		addTestUserToDatabase("1", "test1");
		addTestUserToDatabase("2", "test2");
		assertThat(studyRoomMongoRepository.findById("2"))
			.isEqualTo(new User("2", "test2"));
	}
	
	@Test
	public void testSave() {
		User user = new User("1", "added student");
		studyRoomMongoRepository.save(user);
		assertThat(readAllUsersFromDatabase()).containsExactly(user);
	}

	@Test
	public void testDelete() {
		addTestUserToDatabase("1", "test1");
		studyRoomMongoRepository.delete("1");
		assertThat(readAllUsersFromDatabase()).isEmpty();
	}
	
	private void addTestUserToDatabase(String id, String name) {
		userCollection.insertOne(new Document().append("id", id).append("name", name));
	}

	private List<User> readAllUsersFromDatabase() {
		return StreamSupport.stream(userCollection.find().spliterator(), false)
							.map(d -> new User(""+d.get("id"), ""+d.get("name")))
							.collect(Collectors.toList());
	}
}
