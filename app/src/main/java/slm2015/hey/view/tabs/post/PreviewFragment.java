package slm2015.hey.view.tabs.post;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import slm2015.hey.R;
import slm2015.hey.core.issue.IssueHandler;
import slm2015.hey.entity.Issue;
import slm2015.hey.view.component.Card;
import slm2015.hey.view.component.Wizard;

public class PreviewFragment extends Fragment {
    private static final int PICK_IMAGE = 0;
    private ImageButton locationButton;
    private ImageButton cameraButton;
    private ImageButton cancelButton;
    private ImageButton raiseButton;
    private Card card;
    private Issue issue;
    private Wizard wizard;
    private OnPreviewFinishListener previewFinishListener;

    public static PreviewFragment newInstance(Issue issue, Wizard wizard) {
        PreviewFragment fragment = new PreviewFragment();
        fragment.wizard = wizard;
        fragment.issue = issue;
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK && requestCode == PreviewFragment.PICK_IMAGE) {
            Bitmap image;
            try {
                if (intent.getExtras() != null) {
                    image = intent.getExtras().getParcelable("data");
                } else {
                    Uri selectedImage = intent.getData();
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    image = BitmapFactory.decodeStream(imageStream);
                }
                this.issue.setImage(image);
                this.card.assignIssue(issue);
            } catch (Exception e) {
                e.printStackTrace();
                //todo handle error;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.raise_issue, container, false);
        this.card = (Card) view.findViewById(R.id.preview);
        this.card.assignIssue(this.issue);
        this.initOnCreateView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initOnCreateView(View view) {
        initializeLocationButton(view);
        initializeCameraButton(view);
        initializeCancelButton(view);
        initializeRaiseButton(view);
    }

    private void initializeLocationButton(View view) {
        this.locationButton = (ImageButton) view.findViewById(R.id.locationButton);
        this.locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationButton.setSelected(!locationButton.isSelected());
                PreviewFragment.this.card.setIncognito(locationButton.isSelected());
            }
        });
    }

    private void initializeRaiseButton(View view) {
        this.raiseButton = (ImageButton) view.findViewById(R.id.raiseButton);
        this.raiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueHandler issueHandler = new IssueHandler(PreviewFragment.this.getActivity());

                //todo - raise issue api need privacy info
                issueHandler.raise(PreviewFragment.this.issue, false, new IssueHandler.RaiseIssueHandlerCallback() {
                    @Override
                    public void onRaisedIssue() {
                        PreviewFragment.this.previewFinishListener.OnPreviewFinish();
                        Toast.makeText(PreviewFragment.this.getActivity(), "發送成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initializeCameraButton(View view) {
        this.cameraButton = (ImageButton) view.findViewById(R.id.cameraButton);
        this.cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Intent> initialIntent = new ArrayList<Intent>();
                final Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                final Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (initialIntent.size() == 0) {
                    initialIntent.add(galleryIntent);
                }

                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(cameraIntent, "Select Source");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntent.toArray(new Parcelable[initialIntent.size()]));
                cameraIntent.putExtra(Intent.EXTRA_TITLE, "test");
                cameraIntent.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                startActivityForResult(chooserIntent, PreviewFragment.PICK_IMAGE);
            }
        });
    }

    private void initializeCancelButton(View view) {
        this.cancelButton = (ImageButton) view.findViewById(R.id.cancelButton);
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wizard.back();
            }
        });
    }

    public void setOnPreviewFinishListener(OnPreviewFinishListener listener) {
        this.previewFinishListener = listener;
    }

    public interface OnPreviewFinishListener {
        void OnPreviewFinish();
    }

    public void reassignCard(Issue issue) {
        this.issue = issue;
        this.card.assignIssue(issue);
    }
}
