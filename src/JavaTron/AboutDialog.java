/* Copyright (C) 2001 Taylor Gautier
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation; either version 2 of the License, or (at your 
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: AboutDialog.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** 
 * JavaTron About Dialog -- display legal notices and stuff.
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $
 */
public class AboutDialog extends JDialog implements ActionListener
{
  private static final float TEXT_SIZE = (float) 10.0;
  
  public AboutDialog(Frame owner, String label)
  {
    super (owner, label, true);
    setResizable(false);

    // setup the content pane
    getContentPane().setLayout(new BorderLayout());   
    getContentPane().add(new JPanel(), "North");
    getContentPane().add(new JPanel(), "South");
    getContentPane().add(new JPanel(), "East");
    getContentPane().add(new JPanel(), "West");

    // setup content pane
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    getContentPane().add(content, "Center");

    // setup center panel which contains labels and text field
    JPanel center = new JPanel();
    center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
    content.add(center, "Center");

    // setup labels border
    JPanel labelBorder = new JPanel();
    labelBorder.setLayout(new FlowLayout(FlowLayout.LEFT));
    labelBorder.setBorder(BorderFactory.createEtchedBorder());
    center.add(labelBorder);

    // setup the labels
    JPanel labels = new JPanel();    
    labels.setLayout(new GridLayout(0,1));
    addLabels(labels);
    labelBorder.add(labels);

    // buffer panel between labels and text area
    JPanel filler = new JPanel();
    center.add(filler);

    addText(center);

    // buffer panel between text area and button
    filler = new JPanel();
    center.add(filler);

    // add the ok button that dismisses the dialog
    JButton ok = new JButton("Ok");
    ok.addActionListener(this);
    content.add(ok, "South");

    // finally set the size
    setSize(550, 350);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    setVisible(false);
  }
  
  public static void addLabels(Container c)
  {
    c.add(GUIUtilities.createJLabel("Copyright (C) 2001-2002, Taylor Gautier"));
	c.add(GUIUtilities.createJLabel("Copyright (C) 2010, Joe Culbreth"));
    c.add(GUIUtilities.createJLabel("JavaTron Version: " + Version.version));
	c.add(GUIUtilities.createJLabel(""));
	c.add(GUIUtilities.createJLabel("Tested Against Firmware: 3.2.25"));
    c.add(GUIUtilities.createJLabel(""));
    c.add(GUIUtilities.createJLabel("This free software provided under GPL License and"));
    c.add(GUIUtilities.createJLabel("comes with ABSOLUTELY NO WARRANTY."));
    c.add(GUIUtilities.createJLabel(""));
    c.add(GUIUtilities.createJLabel("Usage: java -jar javatron-" + Version.version + ".jar [ip] [user] [pass]"));
  }
  
  public static void addText(Container c)
  {    
    // setup the text area to hold the license
    JTextArea text = new JTextArea(license);
    JScrollPane scroll = new JScrollPane(text);
    text.setLineWrap(false);
    text.setEditable(false);
    GUIUtilities.setFontSize(text, (float) TEXT_SIZE);
    c.add(scroll);
  }
  

                                  
  public static String license = 
 "The GNU General Public License (GPL)\n" + 
 "Version 2, June 1991\n" +
 "Copyright (C) 1989, 1991 Free Software Foundation, Inc.\n" +
 "59 Temple Place, Suite 330, Boston, MA 02111-1307 USA\n" +
 "\n" +
 "Everyone is permitted to copy and distribute verbatim copies\n" +
 "of this license document, but changing it is not allowed.\n" +
 "\n" +
 "Preamble\n" +
 "\n" +
 "The licenses for most software are designed to take away your freedom\n" +
 "to share and change it. By contrast, the GNU General Public License is\n" +
 "intended to guarantee your freedom to share and change free\n" +
 "software--to make sure the software is free for all its users. This\n" +
 "General Public License applies to most of the Free Software\n" +
 "Foundation's software and to any other program whose authors commit to\n" +
 "using it. (Some other Free Software Foundation software is covered by\n" +
 "the GNU Library General Public License instead.) You can apply it to\n" +
 "your programs, too.\n" +
 "\n" +
 "When we speak of free software, we are referring to freedom, not price.\n" +
 "Our General Public Licenses are designed to make sure that you have the\n" +
 "freedom to distribute copies of free software (and charge for this\n" +
 "service if you wish), that you receive source code or can get it if you\n" +
 "want it, that you can change the software or use pieces of it in new\n" +
 "free programs; and that you know you can do these things.\n" +
 "\n" +
 "To protect your rights, we need to make restrictions that forbid anyone\n" +
 "to deny you these rights or to ask you to surrender the rights. These\n" +
 "restrictions translate to certain responsibilities for you if you\n" +
 "distribute copies of the software, or if you modify it.\n" +
 "\n" +
 "For example, if you distribute copies of such a program, whether gratis\n" +
 "or for a fee, you must give the recipients all the rights that you\n" +
 "have. You must make sure that they, too, receive or can get the source\n" +
 "code. And you must show them these terms so they know their rights.\n" +
 "\n" +
 "We protect your rights with two steps: (1) copyright the software, and\n" +
 "(2) offer you this license which gives you legal permission to copy,\n" +
 "distribute and/or modify the software.\n" +
 "\n" +
 "Also, for each author's protection and ours, we want to make certain\n" +
 "that everyone understands that there is no warranty for this free\n" +
 "software. If the software is modified by someone else and passed on, we\n" +
 "want its recipients to know that what they have is not the original, so\n" +
 "that any problems introduced by others will not reflect on the original\n" +
 "authors' reputations.\n" +
 "\n" +
 "Finally, any free program is threatened constantly by software patents.\n" +
 "We wish to avoid the danger that redistributors of a free program will\n" +
 "individually obtain patent licenses, in effect making the program\n" +
 "proprietary. To prevent this, we have made it clear that any patent\n" +
 "must be licensed for everyone's free use or not licensed at all.\n" +
 "\n" +
 "The precise terms and conditions for copying, distribution and\n" +
 "modification follow.\n" +
 "\n" +
 "TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\n" +
 "0. This License applies to any program or other work which contains a\n" +
 "notice placed by the copyright holder saying it may be distributed\n" +
 "under the terms of this General Public License. The \"Program\", below,\n" +
 "refers to any such program or work, and a \"work based on the Program\"\n" +
 "means either the Program or any derivative work under copyright law:\n" +
 "that is to say, a work containing the Program or a portion of it,\n" +
 "either verbatim or with modifications and/or translated into another\n" +
 "language. (Hereinafter, translation is included without limitation in\n" +
 "the term \"modification\".) Each licensee is addressed as \"you\".\n" +
 "\n" +
 "Activities other than copying, distribution and modification are not\n" +
 "covered by this License; they are outside its scope. The act of running\n" +
 "the Program is not restricted, and the output from the Program is\n" +
 "covered only if its contents constitute a work based on the Program\n" +
 "(independent of having been made by running the Program). Whether that\n" +
 "is true depends on what the Program does.\n" +
 "\n" +
 "1. You may copy and distribute verbatim copies of the Program's source\n" +
 "code as you receive it, in any medium, provided that you conspicuously\n" +
 "and appropriately publish on each copy an appropriate copyright notice\n" +
 "and disclaimer of warranty; keep intact all the notices that refer to\n" +
 "this License and to the absence of any warranty; and give any other\n" +
 "recipients of the Program a copy of this License along with the\n" +
 "Program.\n" +
 "\n" +
 "You may charge a fee for the physical act of transferring a copy, and\n" +
 "you may at your option offer warranty protection in exchange for a fee.\n" +
 "\n" +
 "2. You may modify your copy or copies of the Program or any portion of\n" +
 "it, thus forming a work based on the Program, and copy and distribute\n" +
 "such modifications or work under the terms of Section 1 above, provided\n" +
 "that you also meet all of these conditions:\n" +
 "\n" +
 "a) You must cause the modified files to carry prominent notices stating\n" +
 "that you changed the files and the date of any change.\n" +
 "\n" +
 "b) You must cause any work that you distribute or publish, that in\n" +
 "whole or in part contains or is derived from the Program or any part\n" +
 "thereof, to be licensed as a whole at no charge to all third parties\n" +
 "under the terms of this License.\n" +
 "\n" +
 "c) If the modified program normally reads commands interactively when\n" +
 "run, you must cause it, when started running for such interactive use\n" +
 "in the most ordinary way, to print or display an announcement including\n" +
 "an appropriate copyright notice and a notice that there is no warranty\n" +
 "(or else, saying that you provide a warranty) and that users may\n" +
 "redistribute the program under these conditions, and telling the user\n" +
 "how to view a copy of this License. (Exception: if the Program itself\n" +
 "is interactive but does not normally print such an announcement, your\n" +
 "work based on the Program is not required to print an announcement.)\n" +
 "\n" +
 "These requirements apply to the modified work as a whole. If\n" +
 "identifiable sections of that work are not derived from the Program,\n" +
 "and can be reasonably considered independent and separate works in\n" +
 "themselves, then this License, and its terms, do not apply to those\n" +
 "sections when you distribute them as separate works. But when you\n" +
 "distribute the same sections as part of a whole which is a work based\n" +
 "on the Program, the distribution of the whole must be on the terms of\n" +
 "this License, whose permissions for other licensees extend to the\n" +
 "entire whole, and thus to each and every part regardless of who wrote\n" +
 "it.\n" +
 "\n" +
 "Thus, it is not the intent of this section to claim rights or contest\n" +
 "your rights to work written entirely by you; rather, the intent is to\n" +
 "exercise the right to control the distribution of derivative or\n" +
 "collective works based on the Program.\n" +
 "\n" +
 "In addition, mere aggregation of another work not based on the Program\n" +
 "with the Program (or with a work based on the Program) on a volume of a\n" +
 "storage or distribution medium does not bring the other work under the\n" +
 "scope of this License.\n" +
 "\n" +
 "3. You may copy and distribute the Program (or a work based on it,\n" +
 "under Section 2) in object code or executable form under the terms of\n" +
 "Sections 1 and 2 above provided that you also do one of the following:\n" +
 "\n" +
 "a) Accompany it with the complete corresponding machine-readable source\n" +
 "code, which must be distributed under the terms of Sections 1 and 2\n" +
 "above on a medium customarily used for software interchange; or,\n" +
 "\n" +
 "b) Accompany it with a written offer, valid for at least three years,\n" +
 "to give any third party, for a charge no more than your cost of\n" +
 "physically performing source distribution, a complete machine-readable\n" +
 "copy of the corresponding source code, to be distributed under the\n" +
 "terms of Sections 1 and 2 above on a medium customarily used for\n" +
 "software interchange; or,\n" +
 "\n" +
 "c) Accompany it with the information you received as to the offer to\n" +
 "distribute corresponding source code. (This alternative is allowed only\n" +
 "for noncommercial distribution and only if you received the program in\n" +
 "object code or executable form with such an offer, in accord with\n" +
 "Subsection b above.)\n" +
 "\n" +
 "The source code for a work means the preferred form of the work for\n" +
 "making modifications to it. For an executable work, complete source\n" +
 "code means all the source code for all modules it contains, plus any\n" +
 "associated interface definition files, plus the scripts used to control\n" +
 "compilation and installation of the executable. However, as a special\n" +
 "exception, the source code distributed need not include anything that\n" +
 "is normally distributed (in either source or binary form) with the\n" +
 "major components (compiler, kernel, and so on) of the operating system\n" +
 "on which the executable runs, unless that component itself accompanies\n" +
 "the executable.\n" +
 "\n" +
 "If distribution of executable or object code is made by offering access\n" +
 "to copy from a designated place, then offering equivalent access to\n" +
 "copy the source code from the same place counts as distribution of the\n" +
 "source code, even though third parties are not compelled to copy the\n" +
 "source along with the object code.\n" +
 "\n" +
 "4. You may not copy, modify, sublicense, or distribute the Program\n" +
 "except as expressly provided under this License. Any attempt otherwise\n" +
 "to copy, modify, sublicense or distribute the Program is void, and will\n" +
 "automatically terminate your rights under this License. However,\n" +
 "parties who have received copies, or rights, from you under this\n" +
 "License will not have their licenses terminated so long as such parties\n" +
 "remain in full compliance.\n" +
 "\n" +
 "5. You are not required to accept this License, since you have not\n" +
 "signed it. However, nothing else grants you permission to modify or\n" +
 "distribute the Program or its derivative works. These actions are\n" +
 "prohibited by law if you do not accept this License. Therefore, by\n" +
 "modifying or distributing the Program (or any work based on the\n" +
 "Program), you indicate your acceptance of this License to do so, and\n" +
 "all its terms and conditions for copying, distributing or modifying the\n" +
 "Program or works based on it.\n" +
 "\n" +
 "6. Each time you redistribute the Program (or any work based on the\n" +
 "Program), the recipient automatically receives a license from the\n" +
 "original licensor to copy, distribute or modify the Program subject to\n" +
 "these terms and conditions. You may not impose any further restrictions\n" +
 "on the recipients' exercise of the rights granted herein. You are not\n" +
 "responsible for enforcing compliance by third parties to this License.\n" +
 "\n" +
 "7. If, as a consequence of a court judgment or allegation of patent\n" +
 "infringement or for any other reason (not limited to patent issues),\n" +
 "conditions are imposed on you (whether by court order, agreement or\n" +
 "otherwise) that contradict the conditions of this License, they do not\n" +
 "excuse you from the conditions of this License. If you cannot\n" +
 "distribute so as to satisfy simultaneously your obligations under this\n" +
 "License and any other pertinent obligations, then as a consequence you\n" +
 "may not distribute the Program at all. For example, if a patent license\n" +
 "would not permit royalty-free redistribution of the Program by all\n" +
 "those who receive copies directly or indirectly through you, then the\n" +
 "only way you could satisfy both it and this License would be to refrain\n" +
 "entirely from distribution of the Program.\n" +
 "\n" +
 "If any portion of this section is held invalid or unenforceable under\n" +
 "any particular circumstance, the balance of the section is intended to\n" +
 "apply and the section as a whole is intended to apply in other\n" +
 "circumstances.\n" +
 "\n" +
 "It is not the purpose of this section to induce you to infringe any\n" +
 "patents or other property right claims or to contest validity of any\n" +
 "such claims; this section has the sole purpose of protecting the\n" +
 "integrity of the free software distribution system, which is\n" +
 "implemented by public license practices. Many people have made generous\n" +
 "contributions to the wide range of software distributed through that\n" +
 "system in reliance on consistent application of that system; it is up\n" +
 "to the author/donor to decide if he or she is willing to distribute\n" +
 "software through any other system and a licensee cannot impose that\n" +
 "choice.\n" +
 "\n" +
 "This section is intended to make thoroughly clear what is believed to\n" +
 "be a consequence of the rest of this License.\n" +
 "\n" +
 "8. If the distribution and/or use of the Program is restricted in\n" +
 "certain countries either by patents or by copyrighted interfaces, the\n" +
 "original copyright holder who places the Program under this License may\n" +
 "add an explicit geographical distribution limitation excluding those\n" +
 "countries, so that distribution is permitted only in or among countries\n" +
 "not thus excluded. In such case, this License incorporates the\n" +
 "limitation as if written in the body of this License.\n" +
 "\n" +
 "9. The Free Software Foundation may publish revised and/or new versions\n" +
 "of the General Public License from time to time. Such new versions will\n" +
 "be similar in spirit to the present version, but may differ in detail\n" +
 "to address new problems or concerns.\n" +
 "\n" +
 "Each version is given a distinguishing version number. If the Program\n" +
 "specifies a version number of this License which applies to it and \"any\n" +
 "later version\", you have the option of following the terms and\n" +
 "conditions either of that version or of any later version published by\n" +
 "the Free Software Foundation. If the Program does not specify a version\n" +
 "number of this License, you may choose any version ever published by\n" +
 "the Free Software Foundation.\n" +
 "\n" +
 "10. If you wish to incorporate parts of the Program into other free\n" +
 "programs whose distribution conditions are different, write to the\n" +
 "author to ask for permission. For software which is copyrighted by the\n" +
 "Free Software Foundation, write to the Free Software Foundation; we\n" +
 "sometimes make exceptions for this. Our decision will be guided by the\n" +
 "two goals of preserving the free status of all derivatives of our free\n" +
 "software and of promoting the sharing and reuse of software generally.\n" +
 "\n" +
 "NO WARRANTY\n" +
 "\n" +
 "11. BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO\n" +
 "WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW.\n" +
 "EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR\n" +
 "OTHER PARTIES PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND,\n" +
 "EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n" +
 "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE\n" +
 "ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH\n" +
 "YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL\n" +
 "NECESSARY SERVICING, REPAIR OR CORRECTION.\n" +
 "\n" +
 "12. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN\n" +
 "WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY\n" +
 "AND/OR REDISTRIBUTE THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU\n" +
 "FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR\n" +
 "CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE\n" +
 "PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING\n" +
 "RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A\n" +
 "FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF\n" +
 "SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH\n" +
 "DAMAGES.\n" +
 "\n" +
 "END OF TERMS AND CONDITIONS\n";  
}