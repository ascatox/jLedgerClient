package it.eng.jledgerclient.fabric.config;

import it.eng.jledgerclient.exception.JLedgerClientException;
import it.eng.jledgerclient.fabric.helper.ChannelInitializationManager;
import it.eng.jledgerclient.fabric.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.IdentityException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author ascatox
 */
public class UserManager {
    private final static Logger log = LogManager.getLogger(UserManager.class);

    private static UserManager instance;
    private Configuration configuration;
    private Organization organization;
    private Certificates certificates;

    private UserManager(Configuration configuration, Certificates certificates, Organization organization) {
        this.configuration = configuration;
        this.organization = organization;
        this.certificates = certificates;
    }

    public static UserManager getInstance(Configuration configuration, Certificates certificates, Organization organization) throws JLedgerClientException, InvalidArgumentException {
        if (instance == null || !instance.organization.equals(organization)) { //1
            synchronized (ChannelInitializationManager.class) {
                if (instance == null || !instance.organization.equals(organization)) {  //2
                    instance = new UserManager(configuration, certificates, organization);
                }
            }
        }
        return instance;
    }


    public void completeUsers() throws JLedgerClientException {
        try {
            Set<User> users = organization.getUsers();
            for (User user : users) {
                doCompleteUser(user);
            }
        } catch (IOException | NoSuchProviderException | NoSuchAlgorithmException
                | InvalidKeySpecException | IdentityException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
            log.error(e);
            throw new JLedgerClientException(e);
        }
    }

    private void doCompleteUser(User user) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, IdentityException {
        user.setMspId(organization.getMspID());
        String certificate = null;
        PrivateKey privateKey = null;
        if (!certificates.areGiven()) {
            File certConfigPath = Utils.getCertConfigPath(organization.getDomainName(), user, configuration.getCryptoconfigdir());
            certificate = new String(IOUtils.toByteArray(new FileInputStream(certConfigPath)), ConfigManager.UTF_8);
            File fileSk = Utils.findFileSk(organization.getDomainName(), user, configuration.getCryptoconfigdir());
            privateKey = Utils.getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(fileSk)));
        } else {
            certificate = new String(IOUtils.toByteArray(certificates.getCertificate()), ConfigManager.UTF_8);
            privateKey = Utils.getPrivateKeyFromBytes(IOUtils.toByteArray(certificates.getKeystore()));
        }
        user.setEnrollment(new Enrollment(privateKey, certificate));
        //getUserAttributes(user, organization.getCa());
    }

    private void getUserAttributes(User user, Ca ca) throws org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, IdentityException {
        final Collection<HFCAIdentity> hfcaIdentities = ca.getCaClient().getHFCAIdentities(user);
        if (null != hfcaIdentities && !hfcaIdentities.isEmpty()) {
            List<HFCAIdentity> listIds = (List) hfcaIdentities;
            HFCAIdentity identity = (null == listIds || listIds.isEmpty() ? null : listIds.get(0));
            Collection<Attribute> attributes = identity.getAttributes();
            if (null != attributes && !attributes.isEmpty()) {
                user.setAttributes(attributes);
            }
        }
    }

    private void enrollUser(User user, Ca ca) throws JLedgerClientException {
        try {
            if (StringUtils.isEmpty(user.getSecret()) || null == ca) {
                throw new JLedgerClientException("Secret for user: " + user.getName() + " not given or error in CA retrieving!!!");
            }
            final org.hyperledger.fabric.sdk.Enrollment enrollment = ca.getCaClient().enroll(user.getName(), user.getSecret());
            if (null == enrollment) {
                throw new JLedgerClientException("User: " + user.getName() + " not correctly enrolled or problems enrolling!!!");

            }
            user.setEnrollment(new Enrollment(enrollment.getKey(), enrollment.getCert()));
        } catch (EnrollmentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
            throw new JLedgerClientException(e);
        }
    }


}
