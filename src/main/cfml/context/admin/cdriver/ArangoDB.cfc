/**
 * ArangoDB Cache Driver
 * 
 * @author jesse.shaffer
 * @date 2/7/14
 **/
component displayname="ArangoDB" extends="Cache" {

	fields = [
		 group("Server","Connection information for the server")
			,field("Host Name"	,"host"		,"127.0.0.1"	,true	,"The ArangoDB server."						,"text"	,"true")
			,field("Port"		,"port"		,"8529"			,true	,"The port that ArangoDB is listening on."	,"text"	,"true")
			,field("Database"	,"database"	,"_system"		,true	,"The database to create/use to."			,"text"	,"true")
		
		,group("Authentication","Username/password information.")
			,field("User"		,"user"		,"root"	,true	,"The user name to connect with. This is ignored when server authentication is disabled."	,"text"		,"true")
			,field("Password"	,"password"	,""		,false	,"The password to connect with. This is ignored when server authentication is disabled."	,"password"	,"true")
	];

	/**
	 *
	 **/
	public string function getClass() {
		return "org.jdsnet.arangodb.cache.railo.ArangoDBCache";
	}

	/**
	 * 
	 **/
	public string function getLabel() output=false {
		return "ArangoDB";
	}

	/**
	 *
	 **/
	public string function getDescription() output="no" {
		var c="";
		savecontent variable="c" {
			writeoutput("ArangoDB is a multi-model database that support schema-less schemas.");
		}
		return c;
	}

}