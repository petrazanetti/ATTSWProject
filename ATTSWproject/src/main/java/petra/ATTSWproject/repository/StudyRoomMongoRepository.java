package petra.ATTSWproject.repository;

import petra.ATTSWproject.model.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;


public class StudyRoomMongoRepository implements StudyRoomRepository {

	private MongoCollection<Document> userCollection;
	
	public StudyRoomMongoRepository(MongoClient client,String databaseName, String collectionName) {
		userCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}
	
	@Override
	public List<User> findAll() {
		return StreamSupport.
				stream(userCollection.find().spliterator(), false)
				.map(this::fromDocumentToUser)
				.collect(Collectors.toList());
	}

	@Override
	public User findById(String id) {
		Document user = userCollection.find(Filters.eq("id", id)).first();
		if (user != null)
			return new User(""+user.get("id"), ""+user.get("name"));
		return null;		
	}

	@Override
	public void save(User user) {
		Document docUser = new Document().append("id",user.getId()).append("name",user.getName());
		userCollection.insertOne(docUser);
		
	}

	@Override
	public void delete(String id) {
		userCollection.deleteOne(Filters.eq("id", id));
	}
	
	private User fromDocumentToUser(Document d) {
		return new User(""+d.get("id"), ""+d.get("name"));
	}
	
	

}
