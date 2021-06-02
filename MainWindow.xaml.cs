using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace ModifyFileHash
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        private void textBox_PreviewDragOver(object sender, DragEventArgs e)
        {
            e.Effects = DragDropEffects.Copy;
            e.Handled = true;
        }

        private void textBox_PreviewDrop(object sender, DragEventArgs e)
        {
            foreach (string filename in (string[])e.Data.GetData(DataFormats.FileDrop))
            {
                if (!string.IsNullOrWhiteSpace(textBox.Text) && !textBox.Text.EndsWith(Environment.NewLine))
                {
                    textBox.Text += Environment.NewLine;
                }
                string[] result = Regex.Split(textBox.Text, "\r\n|\r|\n");
                if (!result.Contains(filename))
                {
                    textBox.Text += filename;
                }
            }
        }

        private void btnModify_Click(object sender, RoutedEventArgs e)
        {
            string[] result = Regex.Split(textBox.Text, "\r\n|\r|\n");
            foreach (string filename in result)
            {
                if (!string.IsNullOrWhiteSpace(filename.Trim()) && File.Exists(filename.Trim()))
                {
                    string new_file = filename;
                    int dot = filename.LastIndexOf('.');
                    if (dot > 0)
                    {
                        new_file = new_file.Insert(dot, "_modify");
                    }
                    else
                    {
                        new_file = new_file + "_modify";
                    }
                    while (File.Exists(new_file))
                    {
                        int index = new_file.LastIndexOf("_modify");
                        new_file = new_file.Insert(index + 7, "_");
                    }
                    try
                    {
                        File.Copy(filename, new_file);
                        using (StreamWriter sw = new StreamWriter(new_file, true))
                        {
                            sw.Write(DateTime.Now.ToString() + " modified by Himmelt.");
                            sw.Close();
                        }
                    }
                    catch (Exception exp)
                    {
                        textBox.Text += Environment.NewLine + exp.ToString();
                    }
                }
            }
            textBox.Text += "\n全部修改完毕！";
        }

        private void btnAdd_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog ofd = new OpenFileDialog();
            ofd.ValidateNames = true;
            ofd.CheckPathExists = true;
            ofd.CheckFileExists = true;
            ofd.Multiselect = true;
            if (ofd.ShowDialog() == true)
            {
                foreach (string filename in ofd.FileNames)
                {
                    if (!string.IsNullOrWhiteSpace(textBox.Text) && !textBox.Text.EndsWith(Environment.NewLine))
                    {
                        textBox.Text += Environment.NewLine;
                    }
                    string[] result = Regex.Split(textBox.Text, "\r\n|\r|\n");
                    if (!result.Contains(filename))
                    {
                        textBox.Text += filename;
                    }
                }
            }
        }

        private void btnClear_Click(object sender, RoutedEventArgs e)
        {
            textBox.Clear();
        }
    }
}
