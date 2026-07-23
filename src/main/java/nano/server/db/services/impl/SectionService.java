package nano.server.db.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nano.server.db.access.DBException;
import nano.server.db.daos.SectionDao;
import nano.server.db.entities.Section;
import nano.server.db.services.def.IRoleService;
import nano.server.db.services.def.ISectionService;

@Service
public class SectionService implements ISectionService {
	@Autowired
	private IRoleService roleService;

	@Override
	public List<Section> allSections() throws DBException {
		SectionDao dao = new SectionDao(roleService);
		
		String search = null;
		
		int total = dao.countEntities(search);
		
		return dao.allEntities(null, null, total, 0);
	}

	@Override
	public List<Section> allSections(String search, String order, int limit, int offset) throws DBException {
		SectionDao dao = new SectionDao(roleService);
		
		return dao.allEntities(search, order, limit, offset);
	}

	@Override
	public int countSections(String search) throws DBException {
		SectionDao dao = new SectionDao();
		
		return dao.countEntities(search);
	}

	@Override
	public Section getSection(String name) throws DBException {
		SectionDao dao = new SectionDao(roleService);
		
		return dao.getEntity(name);
	}

	@Override
	public void setSection(Section section) throws DBException {
		SectionDao dao = new SectionDao();
		
		dao.setEntity(section);
	}

	@Override
	public void delSection(String name) throws DBException {
		SectionDao dao = new SectionDao();
		
		dao.delEntity(name);
	}
}
