package it.eng.jledgerclient.fabric.utils;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import it.eng.jledgerclient.exception.JLedgerClientException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class JsonConverter {
    private final static Logger log = LogManager.getLogger(JsonConverter.class);

    public static String convertToJson(Object obj) throws JLedgerClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE); //This property put data in upper camel case
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error(e);
            throw new JLedgerClientException(e);
        }
    }

    public static String convertToJsonNode(Object obj) throws JLedgerClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error(e);
            throw new JLedgerClientException(e);
        }
    }

    public static Object convertFromJson(String json, Class clazz, boolean isCollection) throws JLedgerClientException {
        try {
            if (StringUtils.isEmpty(json))
                throw new JLedgerClientException("Json data is EMPTY");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true); //This property serialize/deserialize not considering the case of fields
            if (isCollection) {
                ObjectReader objectReader = mapper.reader().forType(new TypeReference<List<?>>() {
                });
                return objectReader.readValue(json);
            }
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error(e);
            throw new JLedgerClientException(e);
        }
    }

}


