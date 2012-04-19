/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sleuthkit.autopsy.report;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Document.*;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.ingest.IngestManager;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.ContentVisitor;
import org.sleuthkit.datamodel.Directory;
import org.sleuthkit.datamodel.File;
import org.sleuthkit.datamodel.Image;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskData;
public class reportXML {
    public static Document xmldoc = new Document();
    public reportXML (HashMap<BlackboardArtifact,ArrayList<BlackboardAttribute>> report, reportFilter rr){
        try{
         Case currentCase = Case.getCurrentCase(); // get the most updated case
         SleuthkitCase skCase = currentCase.getSleuthkitCase();
         String caseName = currentCase.getName();
         Integer imagecount = currentCase.getImageIDs().length;
         Integer filesystemcount = currentCase.getRootObjectsCount();
         Integer totalfiles = skCase.countFsContentType(TskData.TSK_FS_META_TYPE_ENUM.TSK_FS_META_TYPE_REG);
         Integer totaldirs = skCase.countFsContentType(TskData.TSK_FS_META_TYPE_ENUM.TSK_FS_META_TYPE_DIR);
         Element root = new Element("Case");
         xmldoc = new Document(root);
         DateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");
         Date date = new Date();
         String datetime = datetimeFormat.format(date);
         String datenotime = dateFormat.format(date);
         Comment comment = new Comment("XML Report Generated by Autopsy 3 on " + datetime);
         root.addContent(comment);
         //Create summary node involving how many of each type
         Element summary = new Element("Summary");
          if(IngestManager.getDefault().isIngestRunning())
            {
              summary.addContent(new Element("Warning").setText("Report was run before ingest services completed!"));
            }
         summary.addContent(new Element("Name").setText(caseName));
         summary.addContent(new Element("Total-Images").setText(imagecount.toString()));
         summary.addContent(new Element("Total-FileSystems").setText(filesystemcount.toString()));
         summary.addContent(new Element("Total-Files").setText(totalfiles.toString()));
         summary.addContent(new Element("Total-Directories").setText(totaldirs.toString()));
         root.addContent(summary);
         //generate the nodes for each of the types so we can use them later
         Element nodeGen = new Element("General-Information");
         Element nodeWebBookmark = new Element("Web-Bookmarks");
         Element nodeWebCookie = new Element("Web-Cookies");
         Element nodeWebHistory = new Element("Web-History");
         Element nodeWebDownload = new Element("Web-Downloads");
         Element nodeRecentObjects = new Element("Recent-Documents");
         Element nodeTrackPoint = new Element("Track-Points");
         Element nodeInstalled = new Element("Installed-Programfiles");
         Element nodeKeyword = new Element("Keyword-Search-Hits");
         Element nodeHash = new Element("Hashset-Hits");
         Element nodeDevice = new Element("Attached-Devices");
         //remove bytes
         Pattern INVALID_XML_CHARS = Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF]");
         for (Entry<BlackboardArtifact,ArrayList<BlackboardAttribute>> entry : report.entrySet()) {
              if(reportFilter.cancel == true){
                         break;
                        }
              int cc = 0;
            Element artifact = new Element("Artifact");
            Long objId = entry.getKey().getObjectID();
            Content cont = skCase.getContentById(objId);
            Long filesize = cont.getSize();
            artifact.setAttribute("ID", objId.toString());
            artifact.setAttribute("Name", cont.accept(new NameVisitor()));
            artifact.setAttribute("Size", filesize.toString());
            
            // Get all the attributes for this guy
             for (BlackboardAttribute tempatt : entry.getValue())
                 {
                      if(reportFilter.cancel == true){
                         break;
                        }
                  Element attribute = new Element("Attribute").setAttribute("Type",tempatt.getAttributeTypeDisplayName());
                  String tempvalue = tempatt.getValueString();
                 //INVALID_XML_CHARS.matcher(tempvalue).replaceAll("");
                  Element value = new Element("Value").setText(tempvalue);
                  attribute.addContent(value);
                  Element context = new Element("Context").setText(StringEscapeUtils.escapeXml(tempatt.getContext()));
                  attribute.addContent(context);
                  artifact.addContent(attribute);
                  cc++;
                 }
             
            if(entry.getKey().getArtifactTypeID() == 1){
                //while (entry.getValue().iterator().hasNext())
               // {
              //  }
                nodeGen.addContent(artifact);
            }
            if(entry.getKey().getArtifactTypeID() == 2){
                
                
                nodeWebBookmark.addContent(artifact);
            }
            if(entry.getKey().getArtifactTypeID() == 3){
                
                nodeWebCookie.addContent(artifact);
            }
            if(entry.getKey().getArtifactTypeID() == 4){
                
                nodeWebHistory.addContent(artifact);
            }
            if(entry.getKey().getArtifactTypeID() == 5){
                 nodeWebDownload.addContent(artifact);
            }
            if(entry.getKey().getArtifactTypeID() == 6){
                 nodeRecentObjects.addContent(artifact);
            }
            if(entry.getKey().getArtifactTypeID() == 7){
                 nodeTrackPoint.addContent(artifact);
            }
            if(entry.getKey().getArtifactTypeID() == 8){
                 nodeInstalled.addContent(artifact);
            }
            if(entry.getKey().getArtifactTypeID() == 9){
                 nodeKeyword.addContent(artifact);
            }
            if(entry.getKey().getArtifactTypeID() == 10){
                 nodeHash.addContent(artifact);
            } 
             if(entry.getKey().getArtifactTypeID() == 11){
                 nodeDevice.addContent(artifact);
            } 
            cc++;
           rr.progBarSet(cc);
            //end of master loop
        }
         
          //add them in the order we want them to the document
            root.addContent(nodeGen);
            root.addContent(nodeWebBookmark);
            root.addContent(nodeWebCookie);
            root.addContent(nodeWebHistory);
            root.addContent(nodeWebDownload);
            root.addContent(nodeRecentObjects);
            root.addContent(nodeTrackPoint);
            root.addContent(nodeInstalled);
            root.addContent(nodeKeyword);
            root.addContent(nodeHash); 
            root.addContent(nodeDevice);
         
            try {
                  FileOutputStream out = new FileOutputStream(currentCase.getCaseDirectory()+"/Reports/" + caseName + "-" + datenotime + ".xml");
                  XMLOutputter serializer = new XMLOutputter();
                  serializer.output(xmldoc, out);
                  out.flush();
                  out.close();
                }
            catch (IOException e) {
              System.err.println(e);
                }

                }   
    catch (Exception e){
        Logger.getLogger(reportXML.class.getName()).log(Level.WARNING, "Exception occurred", e);
    }
  }
    
    private class NameVisitor extends ContentVisitor.Default<String> {

        @Override
        protected String defaultVisit(Content cntnt) {
            throw new UnsupportedOperationException("Not supported for " + cntnt.toString());
        }

        @Override
        public String visit(Directory dir) {
            return dir.getName();
        }

        @Override
        public String visit(Image img) {
            return img.getName();
        }

        @Override
        public String visit(File fil) {
            return fil.getName();
        }
    }
}
