package petra.ATTSWproject.repository;

import petra.ATTSWproject.model.User;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;


public class StudyRoomMongoRepository implements StudyRoomRepository {

	private MongoCollection<Document> userCollection;
	
	public StudyRoomMongoRepository(MongoClient client) {
		userCollection = client.getDatabase("studyRoom").getCollection("user");
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

}
