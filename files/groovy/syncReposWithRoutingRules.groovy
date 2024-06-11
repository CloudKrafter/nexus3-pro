import org.sonatype.nexus.repository.Repository
import org.sonatype.nexus.repository.config.Configuration
import org.sonatype.nexus.repository.manager.RepositoryManager
import org.sonatype.nexus.repository.routing.RoutingRuleStore

class SyncRepositories extends ScriptBaseClass {

    SyncRepositories(context) {
        super(context)
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    Boolean findRepositoryInConfig(Repository repo, List<Map<String, Object>> repositories) {
        return repositories.any { repoConfig ->
            return repo.getType().getValue() == (repoConfig.type as String) &&
                    repo.getFormat().getValue() == (repoConfig.format as String) &&
                    repo.getName() == (repoConfig.name as String)
        }
    }

    Configuration newConfiguration(Map map) {
        RepositoryManager repositoryManager = repository.getRepositoryManager()
        Configuration config

        try {
            config = repositoryManager.newConfiguration()
        } catch (ignored) {
            // Compatibility with nexus versions older than 3.21
            config = Configuration.newInstance()
        }

        config.with {
            repositoryName = map.repositoryName
            recipeName = map.recipeName
            online = Boolean.parseBoolean(map.online as String)
            attributes = map.attributes as Map
        }

        return config
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    boolean configurationEquals(Configuration currentConfig, Configuration configDef) {
        if (currentConfig.attributes.httpclient) {
            if (currentConfig.attributes.httpclient.authentication == [:]) {
                currentConfig.attributes.httpclient.authentication = null
            }
            if (currentConfig.attributes.httpclient.connection == [:]) {
                currentConfig.attributes.httpclient.connection = null
            }
            if (currentConfig.attributes.httpclient == [:]) {
                currentConfig.attributes.httpclient = null
            }
        }

        if (currentConfig.attributes.maven == [:]) {
            currentConfig.attributes.remove('maven')
        }

        return currentConfig.properties == configDef.properties
    }

    void createOrUpdateRepositories(List<Map<String, Serializable>> repos) {
        RepositoryManager repositoryManager = repository.getRepositoryManager()

        repos.each { repoDef ->
            String name = repoDef.proxyname
            Configuration configuration
            def action = scriptResult.newAction(name: name)
            Repository existingRepo = repositoryManager.get(name)

            try {
                log.info('Loading configuration for existing repo {})', name)
                // Load existing repository configuration
                configuration = existingRepo.getConfiguration().copy()

                configuration.routingRuleId = container.lookup(RoutingRuleStore)
                        .getByName(repoDef.name as String)?.id()
               
                if (!configurationEquals(existingRepo.configuration, configuration)) {
                    repositoryManager.update(configuration)
                    log.info('Configuration for repo {} updated', name)
                    scriptResult.addActionDetails(action, ScriptResult.Status.UPDATED)
                } else {
                    log.info('Configuration for repo {} not changed', name)
                    scriptResult.addActionDetails(action, ScriptResult.Status.UNCHANGED)
                }
            } catch (Exception e) {
                log.error('Configuration for repo {} could not be saved: {}', name, e.toString())
                scriptResult.addActionDetails(action, e)
            }
        }
    }

    String execute() {
        List<Map<String, Object>> reposDefs = (config?.routingRules as List<Map<String, Object>>) ?: []
        createOrUpdateRepositories(reposDefs)
        return sendResponse()
    }
}

return new SyncRepositories(this).execute()