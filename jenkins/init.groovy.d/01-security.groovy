// Runs on Jenkins start-up. Creates a local "admin" user with a random password
// (written to JENKINS_HOME/secrets/admin-password.txt) the first time only.
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.security.HudsonPrivateSecurityRealm
import jenkins.model.Jenkins

def jenkins = Jenkins.get()

if (!(jenkins.securityRealm instanceof HudsonPrivateSecurityRealm)) {
    def password = UUID.randomUUID().toString().replace('-', '').substring(0, 16)

    def realm = new HudsonPrivateSecurityRealm(false)
    realm.createAccount('admin', password)
    jenkins.securityRealm = realm

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    strategy.allowAnonymousRead = false
    jenkins.authorizationStrategy = strategy
    jenkins.save()

    new File(jenkins.rootDir, 'secrets/admin-password.txt').text = password
    println 'Created Jenkins user "admin"; password saved in secrets/admin-password.txt'
}
