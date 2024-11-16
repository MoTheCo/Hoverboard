import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VirtualKeyboard extends JFrame {
    private int currentLetterIndex = 0;
    private String[] currentKeys;
    private Timer letterTimer;
    private JPanel keyboardPanel;
    private JLabel[] keyLabels;
    private Robot robot;
    private boolean isMovingRight = true;
    private boolean hasTriggered = false;
    
    private String[] lettersLower = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "ä", "ö", "ü", "ß", "←"," "};
    private String[] lettersUpper = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Ä", "Ö", "Ü", "ß", "←"," "};
    private String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "+", "-", "*", "/", "=", "(", ")", "[", "]", "€", "←"," "};
    private String[] specialChars = {"!", "@", "#", "$", "%", "^", "&", "*", "_", "-", "+", "=", "{", "}", "|", ":", ";", "\"", "'", "?", "/", ".", ",", "<", ">", "~", "°", "§", "\\", "←"," "};

    private JToggleButton capsButton;
    private JToggleButton numbersButton;
    private JToggleButton specialButton;
    
    private Timer controlTimer; // Timer für die Steuerelemente

    public VirtualKeyboard() {
        setTitle("Hoverboard Virtual Keyboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        setAlwaysOnTop(true);

        setUndecorated(false);
        setMinimumSize(new Dimension(600, 200));
        setResizable(true);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        currentKeys = lettersLower;

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        
        capsButton = new JToggleButton("CAPS");
        numbersButton = new JToggleButton("123");
        specialButton = new JToggleButton("#@!");

        Dimension buttonSize = new Dimension(100, 50);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        
        capsButton.setPreferredSize(buttonSize);
        numbersButton.setPreferredSize(buttonSize);
        specialButton.setPreferredSize(buttonSize);
        
        capsButton.setFont(buttonFont);
        numbersButton.setFont(buttonFont);
        specialButton.setFont(buttonFont);

        MouseAdapter buttonHoverAdapter = new MouseAdapter() {
            private Timer buttonTimer;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                JToggleButton button = (JToggleButton)e.getSource();
                
                // Stoppe vorherigen Timer falls vorhanden
                if (buttonTimer != null && buttonTimer.isRunning()) {
                    buttonTimer.stop();
                }
                
                // Erstelle neuen Timer mit 500ms Verzögerung
                buttonTimer = new Timer(500, evt -> {
                    SwingUtilities.invokeLater(() -> {
                        if(button == capsButton) {
                            capsButton.setSelected(!capsButton.isSelected());
                            currentKeys = capsButton.isSelected() ? lettersUpper : lettersLower;
                        } else if(button == numbersButton) {
                            numbersButton.setSelected(!numbersButton.isSelected());
                            if (numbersButton.isSelected()) {
                                currentKeys = numbers;
                                capsButton.setEnabled(false);
                                specialButton.setSelected(false);
                            } else {
                                currentKeys = lettersLower;
                                capsButton.setEnabled(true);
                            }
                        } else if(button == specialButton) {
                            specialButton.setSelected(!specialButton.isSelected());
                            if (specialButton.isSelected()) {
                                currentKeys = specialChars;
                                capsButton.setEnabled(false);
                                numbersButton.setSelected(false);
                            } else {
                                currentKeys = lettersLower;
                                capsButton.setEnabled(true);
                            }
                        }
                        updateKeyLabels();
                        
                        // Verzögerung nach der Aktion
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    });
                });
                buttonTimer.setRepeats(false);
                buttonTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (buttonTimer != null && buttonTimer.isRunning()) {
                    buttonTimer.stop();
                }
            }
        };

        capsButton.addMouseListener(buttonHoverAdapter);
        numbersButton.addMouseListener(buttonHoverAdapter);
        specialButton.addMouseListener(buttonHoverAdapter);

        keyboardPanel = new JPanel(new BorderLayout(3, 3));
        keyboardPanel.setPreferredSize(new Dimension(900, 60));

        JPanel lettersPanel = new JPanel(new GridLayout(1, currentKeys.length, 2, 2));
        keyLabels = new JLabel[currentKeys.length];

        for (int i = 0; i < currentKeys.length; i++) {
            keyLabels[i] = new JLabel(currentKeys[i]);
            keyLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            keyLabels[i].setOpaque(true);
            keyLabels[i].setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            keyLabels[i].setFont(new Font("Arial", Font.BOLD, 16));
            keyLabels[i].setPreferredSize(new Dimension(100, 50));

            final int index = i;
            keyLabels[i].addMouseListener(new MouseAdapter() {
                private Timer letterActivationTimer;
                private static final long LETTER_HOVER_DELAY = 300;
                private long letterHoverStartTime;

                @Override
                public void mouseEntered(MouseEvent e) {
                    letterHoverStartTime = System.currentTimeMillis();
                    letterActivationTimer = new Timer(50, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            if (System.currentTimeMillis() - letterHoverStartTime >= LETTER_HOVER_DELAY) {
                                if (index == currentLetterIndex && !hasTriggered) {
                                    processKey(currentKeys[index]);
                                    hasTriggered = true;
                                    letterActivationTimer.stop();
                                }
                            }
                        }
                    });
                    letterActivationTimer.start();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (letterActivationTimer != null) {
                        letterActivationTimer.stop();
                    }
                    if (index == currentLetterIndex) {
                        hasTriggered = false;
                    }
                }
            });

            lettersPanel.add(keyLabels[i]);
        }

        keyboardPanel.add(lettersPanel, BorderLayout.CENTER);

        JPanel navigationPanel = new JPanel(new BorderLayout(5, 5));
        JButton leftButton = createNavigationButton("◄", false);
        JButton rightButton = createNavigationButton("►", true);
        
        JPanel controlButtonsPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        controlButtonsPanel.add(capsButton);
        controlButtonsPanel.add(numbersButton);
        controlButtonsPanel.add(specialButton);

        navigationPanel.add(leftButton, BorderLayout.WEST);
        navigationPanel.add(controlButtonsPanel, BorderLayout.CENTER);
        navigationPanel.add(rightButton, BorderLayout.EAST);

        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        controlPanel.add(keyboardPanel, BorderLayout.CENTER);
        controlPanel.add(navigationPanel, BorderLayout.SOUTH);

        mainPanel.add(controlPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        letterTimer = new Timer(500, e -> {
            if (isMovingRight) {
                currentLetterIndex = (currentLetterIndex + 1) % currentKeys.length;
            } else {
                currentLetterIndex = (currentLetterIndex - 1 + currentKeys.length) % currentKeys.length;
            }
            hasTriggered = false;
            updateLetterHighlight();
        });

        getRootPane().setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        keyboardPanel.setBackground(new Color(240, 240, 240));

        updateLetterHighlight();
        pack();
        setLocationRelativeTo(null);
    }

    private JButton createNavigationButton(String text, boolean moveRight) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(280, 180));
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isMovingRight = moveRight;
                letterTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                letterTimer.stop();
            }
        });
        return button;
    }

    private void updateKeyLabels() {
        for (int i = 0; i < currentKeys.length && i < keyLabels.length; i++) {
            keyLabels[i].setText(currentKeys[i]);
        }
        currentLetterIndex = 0;
        updateLetterHighlight();
    }

    private void processKey(String key) {
        SwingUtilities.invokeLater(() -> {
            if (key.equals("←")) {
                sendBackspace();
            } else if (key.equals(" ")) {
                sendSpace();
            } else if (!key.isEmpty()) {
                sendKey(key.charAt(0));
            }
            robot.delay(100);
        });
    }

    private void sendSpace() {
        try {
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SPACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLetterHighlight() {
        for (int i = 0; i < keyLabels.length; i++) {
            if (i == currentLetterIndex) {
                keyLabels[i].setBackground(Color.YELLOW);
            } else {
                keyLabels[i].setBackground(null);
            }
        }
    }

    private void sendKey(char character) {
        try {
            robot.delay(50);
            
            switch (character) {
                case 'ä':
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_NUMPAD1);
                    robot.keyRelease(KeyEvent.VK_NUMPAD1);
                    robot.keyPress(KeyEvent.VK_NUMPAD3);
                    robot.keyRelease(KeyEvent.VK_NUMPAD3);
                    robot.keyPress(KeyEvent.VK_NUMPAD2);
                    robot.keyRelease(KeyEvent.VK_NUMPAD2);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    break;
                case 'ö':
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_NUMPAD1);
                    robot.keyRelease(KeyEvent.VK_NUMPAD1);
                    robot.keyPress(KeyEvent.VK_NUMPAD4);
                    robot.keyRelease(KeyEvent.VK_NUMPAD4);
                    robot.keyPress(KeyEvent.VK_NUMPAD8);
                    robot.keyRelease(KeyEvent.VK_NUMPAD8);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    break;
                case 'ü':
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_NUMPAD1);
                    robot.keyRelease(KeyEvent.VK_NUMPAD1);
                    robot.keyPress(KeyEvent.VK_NUMPAD2);
                    robot.keyRelease(KeyEvent.VK_NUMPAD2);
                    robot.keyPress(KeyEvent.VK_NUMPAD9);
                    robot.keyRelease(KeyEvent.VK_NUMPAD9);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    break;
                case 'Ä':
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_NUMPAD1);
                    robot.keyRelease(KeyEvent.VK_NUMPAD1);
                    robot.keyPress(KeyEvent.VK_NUMPAD4);
                    robot.keyRelease(KeyEvent.VK_NUMPAD4);
                    robot.keyPress(KeyEvent.VK_NUMPAD2);
                    robot.keyRelease(KeyEvent.VK_NUMPAD2);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    break;
                case 'Ö':
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_NUMPAD1);
                    robot.keyRelease(KeyEvent.VK_NUMPAD1);
                    robot.keyPress(KeyEvent.VK_NUMPAD5);
                    robot.keyRelease(KeyEvent.VK_NUMPAD5);
                    robot.keyPress(KeyEvent.VK_NUMPAD3);
                    robot.keyRelease(KeyEvent.VK_NUMPAD3);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    break;
                case 'Ü':
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_NUMPAD1);
                    robot.keyRelease(KeyEvent.VK_NUMPAD1);
                    robot.keyPress(KeyEvent.VK_NUMPAD5);
                    robot.keyRelease(KeyEvent.VK_NUMPAD5);
                    robot.keyPress(KeyEvent.VK_NUMPAD4);
                    robot.keyRelease(KeyEvent.VK_NUMPAD4);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    break;
                case 'ß':
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_NUMPAD2);
                    robot.keyRelease(KeyEvent.VK_NUMPAD2);
                    robot.keyPress(KeyEvent.VK_NUMPAD2);
                    robot.keyRelease(KeyEvent.VK_NUMPAD2);
                    robot.keyPress(KeyEvent.VK_NUMPAD5);
                    robot.keyRelease(KeyEvent.VK_NUMPAD5);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    break;
                default:
                    if (Character.isUpperCase(character)) {
                        robot.keyPress(KeyEvent.VK_SHIFT);
                    }
                    
                    int keyCode = getKeyCode(character);
                    if (keyCode != 0) {
                        robot.keyPress(keyCode);
                        robot.delay(50);
                        robot.keyRelease(keyCode);
                    }
                    
                    if (Character.isUpperCase(character)) {
                        robot.keyRelease(KeyEvent.VK_SHIFT);
                    }
            }
            
            robot.delay(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBackspace() {
        try {
            robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);
            robot.delay(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  

    private int getKeyCode(char character) {
        switch (Character.toUpperCase(character)) {
            // Buchstaben - bleiben gleich, da QWERTZ-Layout
            case 'A': return KeyEvent.VK_A;
            case 'B': return KeyEvent.VK_B;
            case 'C': return KeyEvent.VK_C;
            case 'D': return KeyEvent.VK_D;
            case 'E': return KeyEvent.VK_E;
            case 'F': return KeyEvent.VK_F;
            case 'G': return KeyEvent.VK_G;
            case 'H': return KeyEvent.VK_H;
            case 'I': return KeyEvent.VK_I;
            case 'J': return KeyEvent.VK_J;
            case 'K': return KeyEvent.VK_K;
            case 'L': return KeyEvent.VK_L;
            case 'M': return KeyEvent.VK_M;
            case 'N': return KeyEvent.VK_N;
            case 'O': return KeyEvent.VK_O;
            case 'P': return KeyEvent.VK_P;
            case 'Q': return KeyEvent.VK_Q;
            case 'R': return KeyEvent.VK_R;
            case 'S': return KeyEvent.VK_S;
            case 'T': return KeyEvent.VK_T;
            case 'U': return KeyEvent.VK_U;
            case 'V': return KeyEvent.VK_V;
            case 'W': return KeyEvent.VK_W;
            case 'X': return KeyEvent.VK_X;
            case 'Y': return KeyEvent.VK_Z; // Beachte: Y und Z sind vertauscht im deutschen Layout
            case 'Z': return KeyEvent.VK_Y; // Beachte: Y und Z sind vertauscht im deutschen Layout
    
            // Zahlen
            case '0': return KeyEvent.VK_0;
            case '1': return KeyEvent.VK_1;
            case '2': return KeyEvent.VK_2;
            case '3': return KeyEvent.VK_3;
            case '4': return KeyEvent.VK_4;
            case '5': return KeyEvent.VK_5;
            case '6': return KeyEvent.VK_6;
            case '7': return KeyEvent.VK_7;
            case '8': return KeyEvent.VK_8;
            case '9': return KeyEvent.VK_9;
    
            // Deutsche Sonderzeichen
            case 'Ö': return KeyEvent.VK_SEMICOLON;    // ö/Ö ist auf Taste P
            case 'Ä': return KeyEvent.VK_QUOTE;        // ä/Ä ist auf Taste ´
            case 'Ü': return KeyEvent.VK_OPEN_BRACKET; // ü/Ü ist auf Taste [
            case 'ß': return KeyEvent.VK_MINUS;        // ß ist auf Taste -
    
            // Sonderzeichen nach deutscher Tastaturbelegung
            case '.': return KeyEvent.VK_PERIOD;
            case ',': return KeyEvent.VK_COMMA;
            case '-': return KeyEvent.VK_SLASH;
            case '+': return KeyEvent.VK_PLUS;
            case '#': return KeyEvent.VK_NUMBER_SIGN;
            case '<': return KeyEvent.VK_LESS;
            case '>': return KeyEvent.VK_GREATER;
            case '§': return KeyEvent.VK_3;        // Mit Shift
            case '$': return KeyEvent.VK_4;        // Mit Shift
            case '%': return KeyEvent.VK_5;        // Mit Shift
            case '&': return KeyEvent.VK_6;        // Mit Shift
            case '/': return KeyEvent.VK_7;        // Mit Shift
            case '(': return KeyEvent.VK_8;        // Mit Shift
            case ')': return KeyEvent.VK_9;        // Mit Shift
            case '=': return KeyEvent.VK_0;        // Mit Shift
            case '?': return KeyEvent.VK_MINUS;    // Mit Shift
            case '`': return KeyEvent.VK_EQUALS;   // Ohne Shift
            case '´': return KeyEvent.VK_EQUALS;   // Mit Shift
            case '@': return KeyEvent.VK_Q;        // AltGr + Q
            case '€': return KeyEvent.VK_E;        // AltGr + E
            case '[': return KeyEvent.VK_8;        // AltGr + 8
            case ']': return KeyEvent.VK_9;        // AltGr + 9
            case '{': return KeyEvent.VK_7;        // AltGr + 7
            case '}': return KeyEvent.VK_0;        // AltGr + 0
            case '\\': return KeyEvent.VK_LESS;    // AltGr + ß
            case '|': return KeyEvent.VK_LESS;     // AltGr + <
            case '~': return KeyEvent.VK_PLUS;     // AltGr + +
            case ' ': return KeyEvent.VK_SPACE;
    
            default: return 0;
        }
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new VirtualKeyboard().setVisible(true);
        });
    }
}