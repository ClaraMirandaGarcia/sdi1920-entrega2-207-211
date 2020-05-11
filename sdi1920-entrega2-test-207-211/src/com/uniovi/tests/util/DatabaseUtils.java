package com.uniovi.tests.util;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class DatabaseUtils {

	static MongoClient mongoClient;
	private final static String mongoUri = "mongodb://admin:207sdi@tiendamusica-shard-00-00-lpbsd.mongodb.net:27017,tiendamusica-shard-00-01-lpbsd.mongodb.net:27017,tiendamusica-shard-00-02-lpbsd.mongodb.net:27017/test?ssl=true&replicaSet=tiendamusica-shard-0&authSource=admin&retryWrites=true&w=majority";
	private final static String nameDatabase = "test";

	private static MongoDatabase getConnection() {
		MongoClientURI uri = new MongoClientURI(mongoUri);
		mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase(nameDatabase);
		return database;
	}

	public static void closeConnection() {
		mongoClient.close();
	}

	public static void removeUser(String email) {
		// obtener database
		MongoDatabase db = getConnection();
		// obtener coleccion
		MongoCollection<Document> friendshipCollection = db.getCollection("users");

		Bson friendshipFilter = Filters.eq("email", email);
		friendshipCollection.deleteMany(friendshipFilter);
	}

	public static void removeAllFriendshipsOfUserEmail(String emailUser) {

		// obtener ids
		String idUser = getIdFromUserEmail(emailUser);

		// obtener database
		MongoDatabase db = getConnection();

		// obtener coleccion
		MongoCollection<Document> friendshipCollection = db.getCollection("friendship");

		Bson friendshipFilter = Filters.or(Filters.eq("userFrom", new ObjectId(idUser)),
				Filters.eq("userTo", new ObjectId(idUser)));

		friendshipCollection.deleteMany(friendshipFilter);
	}

	public static void removeAllInvitationsOfUserEmail(String emailUser) {

		// obtener ids
		String idUser = getIdFromUserEmail(emailUser);

		// obtener database
		MongoDatabase db = getConnection();

		// obtener coleccion
		MongoCollection<Document> friendshipCollection = db.getCollection("invitations");

		Bson friendshipFilter = Filters.or(Filters.eq("userFrom", new ObjectId(idUser)),
				Filters.eq("userTo", new ObjectId(idUser)));

		friendshipCollection.deleteMany(friendshipFilter);
	}

	public static void removeAllMessagesUsers(String emailUserFrom, String emailUserTo) {
		// obtener database
		MongoDatabase db = getConnection();

		// obtener coleccion
		MongoCollection<Document> messagesCollection = db.getCollection("messages");
		Bson filterDelete = Filters.or(
				Filters.and(Filters.eq("origen", (emailUserFrom)), Filters.eq("destino", (emailUserTo))),
				Filters.and(Filters.eq("origen", (emailUserTo)), Filters.eq("destino", (emailUserFrom))));
		messagesCollection.deleteMany(filterDelete);
	}

	public static int countUsers() {
		// obtener database
		MongoDatabase db = getConnection();
		// obtener coleccion
		MongoCollection<Document> usersCollection = db.getCollection("users");
		return (int) usersCollection.count();
	}

	public static String getIdFromUserEmail(String email) {
		// obtener database
		MongoDatabase db = getConnection();

		// obtener coleccion
		MongoCollection<Document> collection = db.getCollection("users");
		Bson bsonFilter = Filters.eq("email", email);
		FindIterable<Document> docs = collection.find(bsonFilter);
		for (Document doc : docs) {
			return doc.get("_id").toString();
		}
		return null;
	}

	public static void createFriendship(String emailUserFrom, String emailUserTo) {

		// obtener ids
		String idUserFrom = getIdFromUserEmail(emailUserFrom);
		String idUserTo = getIdFromUserEmail(emailUserTo);

		// obtener database
		MongoDatabase db = getConnection();

		// obtener coleccion
		MongoCollection<Document> friendshipCollection = db.getCollection("friendship");
		List<Document> docList = new ArrayList<>();

		Document friendshipDoc = new Document();
		friendshipDoc.append("userTo", new ObjectId(idUserTo));
		friendshipDoc.append("userFrom", new ObjectId(idUserFrom));
		docList.add(friendshipDoc);

		friendshipCollection.insertMany(docList);
	}

	public static void removeFriendship(String emailUserFrom, String emailUserTo) {

		// obtener ids
		String idUserFrom = getIdFromUserEmail(emailUserFrom);
		String idUserTo = getIdFromUserEmail(emailUserTo);

		// obtener database
		MongoDatabase db = getConnection();

		// obtener coleccion
		MongoCollection<Document> friendshipCollection = db.getCollection("friendship");

		Bson friendshipFilter = Filters.or(
				Filters.and(Filters.eq("userFrom", new ObjectId(idUserFrom)),
						Filters.eq("userTo", new ObjectId(idUserTo))),
				Filters.and(Filters.eq("userFrom", new ObjectId(idUserTo)),
						Filters.eq("userTo", new ObjectId(idUserFrom))));

		friendshipCollection.deleteMany(friendshipFilter);
	}

	public static void removeInvitation(String emailUserFrom, String emailUserTo) {

		// obtener ids
		String idUserFrom = getIdFromUserEmail(emailUserFrom);
		String idUserTo = getIdFromUserEmail(emailUserTo);

		// obtener database
		MongoDatabase db = getConnection();

		// obtener coleccion
		MongoCollection<Document> friendshipCollection = db.getCollection("invitations");

		Bson friendshipFilter = Filters.or(
				Filters.and(Filters.eq("userFrom", new ObjectId(idUserFrom)),
						Filters.eq("userTo", new ObjectId(idUserTo))),
				Filters.and(Filters.eq("userFrom", new ObjectId(idUserTo)),
						Filters.eq("userTo", new ObjectId(idUserFrom))));

		friendshipCollection.deleteMany(friendshipFilter);
	}

}
