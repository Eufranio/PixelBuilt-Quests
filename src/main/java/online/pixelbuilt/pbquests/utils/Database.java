package online.pixelbuilt.pbquests.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Frani on 05/09/2017.
 */
public class Database {

    public MongoClient client;
    public MongoDatabase db;

    public Database() {
        this.client = new MongoClient(new MongoClientURI(PixelBuiltQuests.config.getString("database", "URL")));
        this.db = client.getDatabase(PixelBuiltQuests.config.getString("database", "database"));

        try {
            db.getCollection("players");
        } catch (IllegalArgumentException error) {
            db.createCollection("players");
        }

    }

    public int getProgress(Player p, String questLine) {
        FindIterable<Document> doc = db.getCollection("players").find(Filters.eq("UUID", p.getUniqueId().toString()));

        for (Document document : doc) {
            return ((Document)document.get("quests")).getInteger(questLine);
        }

        return 0;
    }

    public void setProgressLevel(Player p, String questLine, int progress) {
        if (db.getCollection("players").count(new BsonDocument("UUID", new BsonString(p.getUniqueId().toString()))) >= 1) {
            db.getCollection("players").updateOne(
                    Filters.eq("UUID", p.getUniqueId().toString()),
                    Updates.set("quests." + questLine, progress));
            return;
        }

        Document document = new Document()
                .append("UUID", p.getUniqueId().toString())
                .append("quests", new Document(questLine, progress));
        db.getCollection("players").insertOne(document);
    }

    public String getQuestLineFromNPC(Entity npc) {
        FindIterable<Document> doc = db.getCollection("npcs").find(
                Filters.eq("UUID", npc.getUniqueId().toString())
        );

        for (Document document : doc) {
            return document.getString("questLine");
        }

        return null;
    }

    public int getQuestIdFromNPC(Entity npc) {
        FindIterable<Document> doc = db.getCollection("npcs").find(
                Filters.eq("UUID", npc.getUniqueId().toString())
        );

        for (Document document : doc) {
            return document.getInteger("questId");
        }

        return -1;
    }

    public void addNpc(String questLine, int questId, Entity npc) {
        if (db.getCollection("npcs").count(new BsonDocument("UUID", new BsonString(npc.getUniqueId().toString()))) >= 1) {

            db.getCollection("npcs").updateOne(
                    Filters.eq("UUID", npc.getUniqueId().toString()),
                    Updates.set("questLine", questLine));

            db.getCollection("npcs").updateOne(
                    Filters.eq("UUID", npc.getUniqueId().toString()),
                    Updates.set("questId", questId));
            return;
        }

        Document document = new Document()
                .append("UUID", npc.getUniqueId().toString())
                .append("questLine", questLine)
                .append("questId", questId);
        db.getCollection("npcs").insertOne(document);
    }

    public void removeNpc(Entity npc) {
        db.getCollection("npcs").deleteOne(
                Filters.eq("UUID", npc.getUniqueId().toString())
        );
    }

}
