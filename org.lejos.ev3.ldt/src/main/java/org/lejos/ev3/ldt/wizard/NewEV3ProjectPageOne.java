package org.lejos.ev3.ldt.wizard;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.lejos.ev3.ldt.container.LeJOSEV3LibContainer;
import org.lejos.ev3.ldt.util.LeJOSEV3Util;

public class NewEV3ProjectPageOne extends NewJavaProjectWizardPageOne {
	@Override
	protected Control createJRESelectionControl(Composite composite) {
		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		gl.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		gl.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		gl.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		
		Group g= new Group(composite, SWT.NONE);
		g.setFont(composite.getFont());
		g.setLayout(gl);
		g.setText("JRE");
		
		Label l = new Label(g, SWT.NONE);
		l.setFont(g.getFont());
		l.setText("Project will use LeJOS EV3 Runtime");
		
		return g;
	}
	
	@Override
	public String getCompilerCompliance()
	{
		// return JavaCore.VERSION_1_6;
		return null;
	}
	
	@Override
	public IClasspathEntry[] getDefaultClasspathEntries() {
		Path lcp = new Path(LeJOSEV3LibContainer.ID+"/"+LeJOSEV3Util.LIBSUBDIR_EV3);
		IClasspathEntry lc = JavaCore.newContainerEntry(lcp);
		
		return new IClasspathEntry[] {lc};
	}
}
