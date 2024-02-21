package in.ineuron.idgenerator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.time.LocalDate;

public class IdGenerator implements IdentifierGenerator {

    //generate custom id for models/entity
    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        String prefix="HB-";
        int mid = (int) (Math.random() * 100000);
        LocalDate currentDate= LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        return prefix+mid+"-"+day+month+year;
    }

}
