package com.datastax.astra.shell;

import static com.datastax.astra.shell.ExitCode.CANNOT_CONNECT;
import static com.datastax.astra.shell.ExitCode.INVALID_PARAMETER;

import org.apache.commons.lang3.StringUtils;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.organizations.domain.Organization;
import com.datastax.astra.sdk.streaming.StreamingClient;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.cmd.BaseCliCommand;
import com.datastax.astra.shell.cmd.BaseCommand;

/**
 * Hold the context of CLI to know where we are.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ShellContext {

    /**
     * Singleton Pattern, private intance.
     */
    private static ShellContext _instance;
    
    /**
     * Default Constructor for Shell.
     */
    private ShellContext() {}
    
    /**
     * Singleton Pattern.
     *
     * @return
     *      current instance of context
     */
    public static synchronized ShellContext getInstance() {
        if (_instance == null) {
            _instance = new ShellContext();
        }
        return _instance;
    }
    
    // -- Config --
    
    /** Current token in use */
    private String token;
    
    /** Configuration file name. */
    private String configFilename = AstraRc.getDefaultConfigurationFileName();
    
    /** Configuration section. */
    private String configSection = AstraRc.ASTRARC_DEFAULT;
    
    /** Load Astra Rc in the context. */
    private AstraRc astraRc;
    
    // -- Clients --
    
    /** Hold a reference for the Api Devops. */
    private DatabasesClient apiDevopsDatabases;
    
    /** Hold a reference for the Api Devops. */
    private OrganizationsClient apiDevopsOrganizations;
    
    /** Hold a reference for the Api Devops. */
    private StreamingClient apiDevopsStreaming;
    
    // -- Selection --
    
    /** Organization informations (prompt). */
    private Organization organization;
    
    /** Work on a db. */
    private Database database;
    
    /** Database informations. */
    private String databaseRegion;
        
    /**
     * Valid section.
     *
     * @param cmd
     *      current command with options
     * @return
     *      section is valid
     */
    private boolean isSectionValid(BaseCommand cmd) {
        if (!this.astraRc.isSectionExists(this.configSection)) {
            cmd.outputError(CANNOT_CONNECT, "No token provided (-t), no config provided (--config), section '" + this.configSection 
                    + "' has not been found in config file '" 
                    + this.astraRc.getConfigFile().getPath() + "'. Try [astra setup]");
            return false;
        }
        return true;
    }
    
    /**
     * Log error.
     *
     * @param cmd
     *      command.
     * @return
     *      error
     */
    private boolean isSectionTokenValid(BaseCommand cmd) {
        if (StringUtils.isEmpty(this.astraRc
                .getSection(this.configSection)
                .get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN))) {
            cmd.outputError(
                    INVALID_PARAMETER, 
                    "Key '" + AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN + 
                    "' has not found been in config [section '" + this.configSection + "']");
            return false;
        }
        return true;
    }
    
    /**
     * Should initialized the client based on provided parameters.
     *
     * @param cli
     *      command line cli
     */
    public void init(BaseCliCommand cli) {
        this.token = cli.getToken();
        
        // No token = use configuration file
        if (this.token == null) {
            // Overriding default config
            if (cli.getConfigFilename() != null) {
                this.astraRc = new AstraRc(cli.getConfigFilename());
            } else {
                this.astraRc = new AstraRc();
            }
            // Overriding default section
            if (!StringUtils.isEmpty(cli.getConfigSectionName())) {
                this.configSection = cli.getConfigSectionName();
            }
            
            if (isSectionValid(cli) && isSectionTokenValid(cli)) {
                token = this.astraRc
                        .getSection(this.configSection)
                        .get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN);
            }
        }
        
        if (token != null) {
            connect(cli, token);
        }
    }
    
    /**
     * Based on a token will initialize the connection.
     * 
     * @param token
     *      token loaded from param
     */
    public void connect(BaseCommand cmd, String token) {

        // Persist Token
        this.token = token;
        
        if (!token.startsWith(token)) {
            cmd.outputError(INVALID_PARAMETER, "Token provided is invalid. It should start with 'AstraCS:...'. Try [astra setup]");
            INVALID_PARAMETER.exit();
        }

        apiDevopsOrganizations  = new OrganizationsClient(token);
        apiDevopsDatabases = new DatabasesClient(token);  
        apiDevopsStreaming = new StreamingClient(token);
        
        try {
            this.organization = apiDevopsOrganizations.organization();
        } catch(Exception e) {
            cmd.outputError(CANNOT_CONNECT, "Token provided is invalid. Try [astra setup]");
            INVALID_PARAMETER.exit();
        }
    }
    
    /**
     * Setter accessor for attribute 'database'.
     * @param database
     *      new value for 'database '
     */
    public void useDatabase(Database database) {
        this.database = database;
        // Initialize to default region
        this.databaseRegion = this.database.getInfo().getRegion();
    }
    
    /**
     * Setter accessor for attribute 'databaseRegion'.
     * @param databaseRegion
     *      new value for 'databaseRegion '
     */
    public void useRegion(String databaseRegion) {
        this.databaseRegion = databaseRegion;
    }
    
    /**
     * Reference if the context has been initialized.
     * 
     * @return
     *      if context is initialized
     */
    public boolean isInitialized() {
        return getToken() != null;
    }
   
    /**
     * Getter accessor for attribute 'token'.
     *
     * @return
     *       current value of 'token'
     */
    public String getToken() {
        return token;
    }

    /**
     * Getter accessor for attribute 'organization'.
     *
     * @return
     *       current value of 'organization'
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Getter accessor for attribute 'database'.
     *
     * @return
     *       current value of 'database'
     */
    public Database getDatabase() {
        return database;
    }
    
    /**
     * Getter accessor for attribute 'databaseRegion'.
     *
     * @return
     *       current value of 'databaseRegion'
     */
    public String getDatabaseRegion() {
        return databaseRegion;
    }
    
    /**
     * Drop focus on current database.
     */
    public void exitDatabase() {
        this.database = null;
        this.databaseRegion = null;
    }

    /**
     * Getter accessor for attribute 'apiDevopsDatabases'.
     *
     * @return
     *       current value of 'apiDevopsDatabases'
     */
    public DatabasesClient getApiDevopsDatabases() {
        return apiDevopsDatabases;
    }

    /**
     * Getter accessor for attribute 'apiDevopsStreaming'.
     *
     * @return
     *       current value of 'apiDevopsStreaming'
     */
    public StreamingClient getApiDevopsStreaming() {
        return apiDevopsStreaming;
    }
    
    /**
     * Getter accessor for attribute 'apiDevopsOrganizations'.
     *
     * @return
     *       current value of 'apiDevopsOrganizations'
     */
    public OrganizationsClient getApiDevopsOrganizations() {
        return apiDevopsOrganizations;
    }

    /**
     * Getter accessor for attribute 'configFilename'.
     *
     * @return
     *       current value of 'configFilename'
     */
    public String getConfigFilename() {
        return configFilename;
    }

    /**
     * Getter accessor for attribute 'configSection'.
     *
     * @return
     *       current value of 'configSection'
     */
    public String getConfigSection() {
        return configSection;
    }

    /**
     * Getter accessor for attribute 'astraRc'.
     *
     * @return
     *       current value of 'astraRc'
     */
    public AstraRc getAstraRc() {
        return astraRc;
    }
    
}