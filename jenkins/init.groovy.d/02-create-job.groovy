// Runs on Jenkins start-up. Creates the "McD-LocationModule" pipeline job that
// builds the Jenkinsfile from the GitHub repository (only if it does not exist yet).
import hudson.plugins.git.BranchSpec
import hudson.plugins.git.GitSCM
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def jobName = 'McD-LocationModule'
def repoUrl = 'https://github.com/108mahima/McD-LocationModule.git'

def jenkins = Jenkins.get()

if (jenkins.getItem(jobName) == null) {
    def scm = new GitSCM(
            GitSCM.createRepoList(repoUrl, null),
            [new BranchSpec('*/main')],
            null, null, [])

    def definition = new CpsScmFlowDefinition(scm, 'Jenkinsfile')
    definition.lightweight = true

    WorkflowJob job = jenkins.createProject(WorkflowJob, jobName)
    job.description = 'McDelivery India - Location module automation (Selenium WebDriver + TestNG + JUnit 5)'
    job.definition = definition
    job.save()

    println "Created pipeline job ${jobName}"
}
