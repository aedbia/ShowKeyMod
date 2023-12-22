package aedbia.showKey.API;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

public class SKPluginCollector {
    public SKPluginCollector(){
        runAllPlugin();
    }
    private void runAllPlugin(){
        List<String> pluginName = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> Type.getType(ShowKeyPlugin.class).equals(a.annotationType())&&ModList.get().isLoaded((String)a.annotationData().get("value")))
                .map(ModFileScanData.AnnotationData::memberName)
                .toList();
        for (String id:pluginName){
            try {
                Class<?> plugin = Class.forName(id);
                if(SKRuleRegisterPlugin.class.isAssignableFrom(plugin)){
                    ((SKRuleRegisterPlugin)(plugin.getDeclaredConstructor().newInstance())).registerRule();

                }
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}