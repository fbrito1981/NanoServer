package nano.server.db.services.def;

import java.util.List;

import nano.server.db.access.DBException;
import nano.server.db.entities.Section;

public interface ISectionService {
	List<Section> allSections() throws DBException;
	
	List<Section> allSections(String search, String order, int limit, int offset) throws DBException;
	
	int countSections(String search) throws DBException;
	
	Section getSection(String name) throws DBException;
	
	void setSection(Section section) throws DBException;
	
	void delSection(String name) throws DBException;
}
