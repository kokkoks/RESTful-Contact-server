package contact.service.mem;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

import contact.entity.Contact;
import contact.entity.XMLKeeper;
import contact.service.*;
import contact.service.mem.MemContactDao;

/**
 * MemDaoFactory is a factory for getting instances of entity DAO object
 * that use memory-based persistence, which isn't really persistence at all!
 * 
 * @see contact.service.DaoFactory
 * @version 2014.09.19
 * @author jim
 */
public class MemDaoFactory extends DaoFactory {
	// singleton instance of this factory
		private static MemDaoFactory factory;
		private MemContactDao daoInstance;
		
		public MemDaoFactory() {
			daoInstance = new MemContactDao();
			
			try {
				JAXBContext jc = JAXBContext.newInstance( XMLKeeper.class );
				Unmarshaller unmarshall = jc.createUnmarshaller();
				File file = new File( "D:\\workspace\\XMLMarshall.xml" );
				XMLKeeper keeper = (XMLKeeper) unmarshall.unmarshal(file);
				for( Contact contact: keeper.getContacts() ){
					daoInstance.save( contact );
				}
				
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public static DaoFactory getInstance() {
			if (factory == null) factory = new MemDaoFactory();
			return factory;
		}
		
		public ContactDao getContactDao() {
			return daoInstance;
		}

		@Override
		public void shutdown() {
			try {
				JAXBContext jc = JAXBContext.newInstance( XMLKeeper.class );
				Marshaller mar = jc.createMarshaller();
				List<Contact> list = getContactDao().findAll();
				XMLKeeper keeper = new XMLKeeper();
				keeper.setContacts(list);
				
				File file = new File( "D:\\workspace\\XMLMarshall.xml" );
				mar.marshal(keeper, file);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
}
