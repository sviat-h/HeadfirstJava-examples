package ServiceServer;

import javax.swing.*;
import java.io.Serializable;

public interface Service extends Serializable {
    public JPanel getGuiPanel();
}

