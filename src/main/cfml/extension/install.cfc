/**
 * install
 * 
 * @author jesse.shaffer
 * @date 2/8/14
 **/
component accessors=true output=true persistent=false {
	
	name	= "CFArangoCache";
	id		= "org.jdsnet.arangodb.cache.railo.ArangoDBCache";
	libs	= [name,"arangodb-java-driver","commons-codec","commons-logging","gson","httpclient","httpcore"];
	
	/**
	 * The method validate is called after a user has submitted the installation form. 
	 * If the installation form consists out of several steps (wizard), the validate method 
	 * is called after each step. Validate will check the data. In case an entry is not valid 
	 * or perhaps missing, a corresponding error can be thrown. By passing the argument error 
	 * the extension notifies Railo about the errors of the validation. The other arguments 
	 * help the method to validate the data. More about these arguments further below.
	 **/
	public void function validate(Struct error, String path, Struct config, Numeric step) output=true {
		return;
	}
	 	
	/**
	 * The method install is called by Railo after the last step of the form has been 
	 * submitted and the validate method has been successfully executed. The method is 
	 * responsible for the installation of the extension by using the data of the passed 
	 * arguments (more on these arguments further below). How this method executes its task 
	 * is up to the method itself.
	 **/
	public String function install(Struct error, String path, Struct config) output=true {
		writeoutput('install from #path#');
		for (var lib in libs) {
			fileCopy(path & "/lib/" & lib & ".jar", getContextPath() & "/lib/" & lib & ".jar");
		}
		fileCopy(path & "/context/admin/cdriver/ArangoDB.cfc", getContextpath() & "/context/admin/cdriver/ArangoDB.cfc");
		
		return "#name# installed successfully.";
	}
	
	/** 
	 * The method update is called by Railo after the last step of the form has been submitted 
	 * and the validate method has been successfully executed. The method is responsible for 
	 * updating an extension by using the data of the passed arguments (more on these arguments 
	 * further below). How this method executes its task is up to the method itself.
	 **/
	public String function update(Struct error, String path, Struct config, Struct previousConfig) output=true {
		uninstall(path,config);
		install(error,path,config);
		
		return "#name# updated successfully.";
	}
	
	/**
	 * The method uninstall is called by Railo when a user has clicked on the uninstall button. 
	 * The method is responsible for uninstalling an extension by using the data of the passed 
	 * arguments (more on these arguments further below).
	 **/
	public String function uninstall(String path, Struct formerConfig) output=true {
		for (var lib in libs) {
			filedelete(getContextPath() & "/lib/" & lib & ".jar");
		}
		//fileDelete(getcontextpath() & "/context/admin/cdriver/ArangoDB.cfc");
		admin	action="removeContext"
				type="#request.adminType#"
				password="#session['password'&request.adminType]#"
				destination="admin/cdriver/#variables.driver#";
				
		return "#name# uninstalled successfully."
	}
	
	
	/**
	 * 
	 **/
	private String function getContextPath() {
		return expandPath("{railo-#request.adminType#}");
	}
}