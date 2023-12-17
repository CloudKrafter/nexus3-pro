"""Default testinfra file for the role."""

import os
import requests

import testinfra.utils.ansible_runner

testinfra_hosts = testinfra.utils.ansible_runner.AnsibleRunner(
    os.environ["MOLECULE_INVENTORY_FILE"]
).get_hosts("nexus")

# set default api url and auth
url = "https://localhost:8091/service/rest/v1/"
auth = ("admin", "changeme")
tls_verify = False

def test_myvar_using_get_variables(host):
    all_variables = host.ansible.get_variables()
    assert 'httpd_setup_enable' in all_variables
    assert all_variables['httpd_setup_enable'] == true

def test_npm_scoped_package_download(host):
    """Test if we can download npm scoped packages."""
    test_package_url = "https://localhost/repository/npm-public/@angular%2fcore"

    get_url_options = (
        f"url='{test_package_url}' dest='/tmp/testfile' force='yes' validate_certs='no'"
    )

    download = host.ansible("get_url", get_url_options, check=False)

    assert download["status_code"] == 200
    assert download["state"] == "file"



def test_api_endpoint():
    """Test if ."""

    # Make a request to the API with SSL verification disabled and authentication
    response = requests.get(url + 'repositories/docker/hosted/docker-private', verify=tls_verify, auth=auth)

    # Assert specific values in the response
    data = response.json()
    assert data["storage"]["writePolicy"] == "ALLOW"
