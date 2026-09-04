package com.javacity.utilities;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class AssetHelper {

    public static AnimComposer findAnimComposer(Spatial spatial) {
        return findAnimComposerWithClips(spatial);
    }

    public static AnimComposer findAnimComposerWithClips(Spatial spatial) {
        AnimComposer composer = spatial.getControl(AnimComposer.class);
        if (composer != null && !composer.getAnimClips().isEmpty()) {
            return composer;
        }
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                AnimComposer childComposer = findAnimComposerWithClips(child);
                if (childComposer != null) {
                    return childComposer;
                }
            }
        }
        // Fallback: if none has clips, return first found composer
        return findFirstAnimComposer(spatial);
    }

    private static AnimComposer findFirstAnimComposer(Spatial spatial) {
        if (spatial.getControl(AnimComposer.class) != null) {
            return spatial.getControl(AnimComposer.class);
        }
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                AnimComposer composer = findFirstAnimComposer(child);
                if (composer != null) {
                    return composer;
                }
            }
        }
        return null;
    }

    public static SkinningControl findSkinningControl(Spatial spatial) {
        if (spatial.getControl(SkinningControl.class) != null) {
            return spatial.getControl(SkinningControl.class);
        }
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                SkinningControl control = findSkinningControl(child);
                if (control != null) {
                    return control;
                }
            }
        }
        return null;
    }

    public static Spatial findChild(Spatial parent, String name) {
        if (name.equals(parent.getName())) {
            return parent;
        }
        if (parent instanceof Node) {
            for (Spatial child : ((Node) parent).getChildren()) {
                Spatial found = findChild(child, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public static void printSceneGraph(Spatial spatial, int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        sb.append(spatial.getName()).append(" (").append(spatial.getClass().getSimpleName()).append(")");
        
        for (int i = 0; i < spatial.getNumControls(); i++) {
            com.jme3.scene.control.Control c = spatial.getControl(i);
            if (c != null) {
                sb.append(" [Control: ").append(c.getClass().getSimpleName()).append("]");
                if (c instanceof AnimComposer) {
                    sb.append(" (Clips: ");
                    for (com.jme3.anim.AnimClip clip : ((AnimComposer)c).getAnimClips()) {
                        sb.append(clip.getName()).append(" ");
                    }
                    sb.append(")");
                }
            }
        }
        
        System.out.println(sb.toString());
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                printSceneGraph(child, depth + 1);
            }
        }
    }

    public static void disableHardwareSkinning(Spatial spatial) {
        SkinningControl control = spatial.getControl(SkinningControl.class);
        if (control != null) {
            control.setHardwareSkinningPreferred(false);
        }
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                disableHardwareSkinning(child);
            }
        }
    }
}
