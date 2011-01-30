
package com.micheleorselli.phpunitannotationcompleter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import javax.swing.Action;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

/**
 * @author Michele Orselli <michele.orselli@gmail.com>
 */
public class PHPUnitAnnotationCompletionDocumentation implements CompletionDocumentation {

    private PHPUnitAnnotationCompletionItem item;

    public PHPUnitAnnotationCompletionDocumentation(PHPUnitAnnotationCompletionItem item) {
        this.item = item;
    }

    @Override
    public String getText() {
         try{
            String resPath = String.format("%s.html", this.item.getText());

            URL zipUrl = this.getClass().getResource(resPath);
            
            InputStream is = zipUrl.openStream();
            byte buffer[] = new byte[1000];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int count = 0;
            do {
                count = is.read(buffer);
                if (count > 0) baos.write(buffer, 0, count);
            } while (count > 0);

            is.close();
            String text = baos.toString();
            baos.close();

            return text;

        } catch (java.io.IOException e){
            return null;
       }
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public CompletionDocumentation resolveLink(String string) {
        return null;
    }

    @Override
    public Action getGotoSourceAction() {
        return null;
    }
    
}