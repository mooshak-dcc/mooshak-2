package pt.up.fc.dcc.mooshak.evaluation;

import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;

public class MooshakSecurityPolicy extends Policy {

	public MooshakSecurityPolicy() {
	}

	@Override
	public PermissionCollection getPermissions(CodeSource codesource) {
		// TODO Auto-generated method stub
		
		System.out.println("Mooshak security: permission requested for CodeSource"+codesource);
		
		return super.getPermissions(codesource);
	}

	@Override
	public PermissionCollection getPermissions(ProtectionDomain domain) {
		// TODO Auto-generated method stub
		
		System.out.println("Mooshak security: permission requested for ProtectionDomain"+domain);
		
		return super.getPermissions(domain);
	}

	
	
}
